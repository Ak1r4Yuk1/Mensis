#!/usr/bin/env python3
import json
import re
from html import unescape
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

SOURCES = [
    {
        "id": "iss_gravidanza",
        "month": 1,
        "url": "https://www.issalute.it/index.php/gravidanza",
        "source": "ISSalute",
        "kind": "articolo",
    },
    {
        "id": "santagostino_gravidanza",
        "month": 2,
        "url": "https://www.santagostino.it/it/santagostinopedia/gravidanza",
        "source": "Santagostino",
        "kind": "articolo",
    },
    {
        "id": "santagostino_ecografia_primo",
        "month": 3,
        "url": "https://www.santagostino.it/it/prestazioni/ecografia-ostetrica-1",
        "source": "Santagostino",
        "kind": "articolo",
    },
    {
        "id": "santagostino_ecografie",
        "month": 4,
        "url": "https://www.santagostino.it/magazine/quante-ecografie-gravidanza/",
        "source": "Santagostino",
        "kind": "articolo",
    },
    {
        "id": "humanitas_alimentazione",
        "month": 5,
        "url": "https://www.humanitasalute.it/in-salute/dieta-e-alimentazione/64220-la-dieta-in-gravidanza/",
        "source": "Humanitas Salute",
        "kind": "articolo",
    },
    {
        "id": "bge_allattamento",
        "month": 6,
        "url": "https://www.ospedalebambinogesu.it/allattamento-al-seno-consigli-pratici-105608/",
        "source": "Bambino Gesu",
        "kind": "video",
    },
    {
        "id": "santagostino_peso",
        "month": 7,
        "url": "https://www.santagostino.it/magazine/aumento-peso-gravidanza/",
        "source": "Santagostino",
        "kind": "articolo",
    },
    {
        "id": "bge_pertosse",
        "month": 8,
        "url": "https://www.ospedalebambinogesu.it/il-vaccino-contro-la-pertosse-in-gravidanza---intervista-al-dott-tozzi-104234/",
        "source": "Bambino Gesu",
        "kind": "video",
    },
    {
        "id": "ministero_gravidanza",
        "month": 9,
        "url": "https://www.salute.gov.it/new/it/tema/salute-della-donna/gravidanza-0/",
        "source": "Ministero della Salute",
        "kind": "articolo",
    },
]

BLOCKED_MARKERS = (
    "requires full cookie support",
    "requires full javascript support",
    "security service to protect itself",
    "enable javascript",
    "gcore",
)


def fetch(url: str) -> str:
    req = Request(
        url,
        headers={
            "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            "Accept-Language": "it-IT,it;q=0.9,en;q=0.5",
            "User-Agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/125 Safari/537.36",
        },
    )
    with urlopen(req, timeout=25) as resp:
        return resp.read().decode("utf-8", errors="ignore")


def clean_html_text(value: str) -> str:
    value = re.sub(r"(?is)<(script|style|noscript|svg|form|nav|footer|header)[^>]*>.*?</\1>", " ", value)
    value = re.sub(r"(?is)<br\s*/?>", "\n", value)
    value = re.sub(r"<[^>]+>", " ", value)
    value = unescape(value)
    value = re.sub(r"[ \t\r\f\v]+", " ", value)
    value = re.sub(r"\n+", "\n", value)
    return value.strip()


def normalize_space(value: str) -> str:
    return re.sub(r"\s+", " ", value).strip()


def extract_title(html: str) -> str:
    for pattern in (
        r'<meta[^>]+property=["\']og:title["\'][^>]+content=["\']([^"\']+)["\']',
        r'<meta[^>]+content=["\']([^"\']+)["\'][^>]+property=["\']og:title["\']',
        r"<h1[^>]*>(.*?)</h1>",
        r"<title[^>]*>(.*?)</title>",
    ):
        match = re.search(pattern, html, re.I | re.S)
        if match:
            return normalize_space(clean_html_text(match.group(1)))
    return ""


def content_region(html: str) -> str:
    for tag in ("article", "main"):
        match = re.search(rf"(?is)<{tag}[^>]*>(.*?)</{tag}>", html)
        if match:
            return match.group(1)
    body = re.search(r"(?is)<body[^>]*>(.*?)</body>", html)
    return body.group(1) if body else html


def is_blocked(html: str, title: str, paragraphs: list[str]) -> bool:
    haystack = " ".join([title, *paragraphs, html[:1200]]).lower()
    return any(marker in haystack for marker in BLOCKED_MARKERS)


def extract_sections(html: str, limit: int = 5) -> list[dict[str, str]]:
    region = content_region(html)
    chunks = re.split(r"(?is)<h[23][^>]*>", region)
    sections = []
    used = set()
    if len(chunks) > 1:
        for chunk in chunks[1:]:
            title_raw, _, body_raw = chunk.partition("</h")
            title = normalize_space(clean_html_text(title_raw))
            paragraphs = extract_paragraphs(body_raw, 3)
            body = " ".join(paragraphs)
            if title and body and body not in used:
                sections.append({"title": title[:90], "body": body[:520]})
                used.add(body)
            if len(sections) >= limit:
                return sections
    for paragraph in extract_paragraphs(region, limit):
        if paragraph not in used:
            sections.append({"title": "Estratto", "body": paragraph[:520]})
            used.add(paragraph)
        if len(sections) >= limit:
            break
    return sections


def extract_paragraphs(html: str, limit: int) -> list[str]:
    found = []
    for raw in re.findall(r"(?is)<p[^>]*>(.*?)</p>", html):
        text = normalize_space(clean_html_text(raw))
        if 90 <= len(text) <= 900 and text not in found:
            if not is_noise(text):
                found.append(text)
        if len(found) >= limit:
            break
    return found


def is_noise(text: str) -> bool:
    low = text.lower()
    noisy = (
        "cookie",
        "privacy",
        "newsletter",
        "prenota",
        "urp@",
        "lunedì",
        "venerdì",
        "clicca sull'immagine",
        "marketing sono disabilitati",
    )
    return any(item in low for item in noisy)


def extract_youtube_links(html: str) -> list[dict[str, str]]:
    patterns = (
        r"https?://(?:www\.)?youtube\.com/embed/([A-Za-z0-9_-]{6,})",
        r"https?://(?:www\.)?youtube-nocookie\.com/embed/([A-Za-z0-9_-]{6,})",
        r"https?://(?:www\.)?youtube\.com/watch\?v=([A-Za-z0-9_-]{6,})",
        r"https?://youtu\.be/([A-Za-z0-9_-]{6,})",
    )
    ids = []
    for pattern in patterns:
        for item in re.findall(pattern, html):
            if item not in ids:
                ids.append(item)
    return [
        {
            "id": video_id,
            "watchUrl": f"https://www.youtube.com/watch?v={video_id}",
            "thumbnail": f"https://img.youtube.com/vi/{video_id}/hqdefault.jpg",
        }
        for video_id in ids
    ]


def scrape_record(source: dict) -> dict:
    base = {
        "id": source["id"],
        "month": source["month"],
        "url": source["url"],
        "source": source["source"],
        "kind": source["kind"],
        "title": "",
        "sections": [],
        "videos": [],
        "status": "ok",
    }
    try:
        html = fetch(source["url"])
        title = extract_title(html)
        sections = extract_sections(html)
        blocked = is_blocked(html, title, [section["body"] for section in sections])
        base.update(
            {
                "title": title,
                "sections": [] if blocked else sections,
                "videos": extract_youtube_links(html),
                "status": "blocked" if blocked else "ok",
            }
        )
    except (HTTPError, URLError, TimeoutError, OSError) as exc:
        base["status"] = f"error: {type(exc).__name__}"
    return base


def main():
    records = [scrape_record(source) for source in SOURCES]
    usable = [item for item in records if item["status"] == "ok" and (item["sections"] or item["videos"])]
    out = Path("app/src/main/assets/pregnancy_sources_it.json")
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(
        json.dumps({"generatedBy": "scrape_pregnancy_sources.py", "records": records, "usable": usable}, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    print(f"{out} ({len(usable)} usable / {len(records)} total)")


if __name__ == "__main__":
    main()
