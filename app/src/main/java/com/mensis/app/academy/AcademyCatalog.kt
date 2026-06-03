package com.mensis.app.academy

import com.mensis.app.academy.AcademyMode.CYCLE
import com.mensis.app.academy.AcademyMode.PREGNANCY
import com.mensis.app.academy.ContentType.ARTICLE
import com.mensis.app.academy.ContentType.VIDEO

/**
 * Catalogo Academy curato e incluso nell'app. Gli articoli sono testo Markdown completo
 * (leggero), i video sono solo ID YouTube riprodotti in-app + anteprima in streaming → APK
 * piccolo. Struttura compatibile con un'eventuale API server-driven futura.
 *
 * I contenuti hanno scopo informativo e non sostituiscono il parere di un professionista
 * sanitario.
 */
object AcademyCatalog {

    val all: List<AcademyContent> get() = cycle + pregnancy

    fun forMode(mode: AcademyMode): List<AcademyContent> = all.filter { it.mode == mode }

    fun categories(mode: AcademyMode): List<String> = forMode(mode).map { it.category }.distinct()

    /** Contenuti in evidenza per la fase corrente (o tutti se la fase non ha tag dedicati). */
    fun featured(mode: AcademyMode, phase: String?): List<AcademyContent> {
        val list = forMode(mode)
        if (phase == null) return list
        val byPhase = list.filter { phase in it.phases }
        return byPhase.ifEmpty { list.filter { it.phases.isEmpty() } }
    }

    fun byId(id: String): AcademyContent? = all.firstOrNull { it.id == id }

    /* ============================== CICLO ============================== */
    private val cycle = listOf(

        // ----------------------------- Fondamenti -----------------------------
        AcademyContent(
            "cyc_fasi", ARTICLE, CYCLE, "Fondamenti",
            title = "Le quattro fasi del ciclo mestruale",
            subtitle = "Mestruale, follicolare, ovulatoria, luteale: cosa accade nel corpo, giorno per giorno.",
            durationMinutes = 7,
            bodyMarkdown = """
# Le quattro fasi del ciclo mestruale

Il ciclo mestruale è molto più della sola mestruazione: è un susseguirsi ritmico di eventi ormonali che coinvolge il cervello (ipotalamo e ipofisi) e le ovaie, e che prepara ogni mese il corpo a una possibile gravidanza. Conoscerlo aiuta a interpretare energia, umore, sonno, pelle e desiderio, oltre che la fertilità.

La durata media è di circa **28 giorni**, ma è del tutto normale che vari **tra 21 e 35 giorni**. Si conta a partire dal **primo giorno di flusso** (giorno 1) fino al giorno prima della mestruazione successiva. Le fasi sono quattro.

## 1. Fase mestruale (in media giorni 1–5)
Inizia con la comparsa del flusso. L'endometrio, lo strato che riveste l'utero e che era stato preparato per accogliere un eventuale embrione, si sfalda e viene espulso. In questa fase **estrogeni e progesterone sono ai minimi**: è frequente sentirsi più stanche, introspettive, con minore spinta sociale. È il momento del **riposo** e dell'ascolto: calore sul ventre, idratazione e movimento dolce aiutano. Il flusso normale dura **3–7 giorni**.

## 2. Fase follicolare (in media giorni 1–13)
Tecnicamente comincia insieme alla mestruazione, ma diventa protagonista quando il flusso termina. L'ipofisi rilascia **FSH** (ormone follicolo-stimolante), che stimola la crescita di alcuni follicoli ovarici; di solito **uno diventa dominante**. I follicoli producono **estrogeni** in quantità crescente. Il risultato è spesso un ritorno di **energia, lucidità mentale, buonumore e socievolezza**, con pelle più luminosa. È la fase ideale per pianificare, iniziare progetti e allenarsi con intensità.

## 3. Fase ovulatoria (intorno al giorno 14 in un ciclo di 28)
L'estrogeno alto innesca un brusco **picco dell'ormone LH** (ormone luteinizzante). Circa **24–36 ore dopo** il follicolo dominante si rompe e libera l'ovulo: è l'**ovulazione**. L'ovulo sopravvive **12–24 ore**; gli spermatozoi possono però vivere **fino a 5 giorni** nelle vie genitali. Per questo la **finestra fertile** comprende i giorni che precedono l'ovulazione e il giorno stesso. Segnali tipici: **muco cervicale trasparente ed elastico** (come albume d'uovo), aumento del desiderio, a volte un lieve dolore da un lato (mittelschmerz).

## 4. Fase luteale (in media giorni 15–28)
Dopo l'ovulazione, il follicolo vuoto si trasforma in **corpo luteo**, che produce **progesterone**. Questo ormone stabilizza l'endometrio e fa **salire leggermente la temperatura basale** (0,2–0,5 °C). Se non c'è fecondazione, il corpo luteo regredisce, progesterone ed estrogeni calano e arriva la mestruazione. Nella seconda parte di questa fase possono comparire i sintomi premestruali (**PMS**): gonfiore, tensione al seno, irritabilità, voglia di dolci.

## Perché tracciare le fasi
Annotare flusso, sintomi, muco e temperatura permette all'app di **stimare la durata media del tuo ciclo**, prevedere la mestruazione successiva e individuare la finestra fertile. Più dati registri con costanza, più le previsioni diventano personalizzate e affidabili.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_ormoni", ARTICLE, CYCLE, "Fondamenti",
            title = "Gli ormoni del ciclo: chi fa cosa",
            subtitle = "FSH, LH, estrogeni e progesterone spiegati in modo semplice.",
            durationMinutes = 6,
            bodyMarkdown = """
# Gli ormoni che governano il ciclo

Dietro il ciclo c'è un dialogo continuo tra **cervello e ovaie**, regolato da quattro ormoni principali. Capire il loro ruolo aiuta a dare un senso a ciò che senti.

## FSH — ormone follicolo-stimolante
Prodotto dall'**ipofisi**, all'inizio del ciclo stimola la crescita dei follicoli ovarici. È lui che "avvia" la fase follicolare.

## Estrogeni
Prodotti dai follicoli in crescita, **aumentano nella prima metà del ciclo**. Favoriscono:
- la maturazione dell'ovulo;
- l'ispessimento dell'endometrio;
- la produzione di muco cervicale fertile;
- spesso buonumore, energia e pelle luminosa.

Il loro picco è il segnale che fa scattare l'ovulazione.

## LH — ormone luteinizzante
Quando gli estrogeni raggiungono il massimo, l'ipofisi rilascia un **picco di LH**. È il "grilletto" che provoca la **rottura del follicolo** e quindi l'ovulazione, 24–36 ore dopo. I test di ovulazione misurano proprio questo ormone.

## Progesterone
Prodotto dal **corpo luteo** dopo l'ovulazione, domina la seconda metà del ciclo. Prepara l'endometrio a un'eventuale gravidanza, **alza la temperatura basale** e ha un effetto calmante ma può favorire gonfiore e sonnolenza. Se non c'è gravidanza, il suo calo provoca la mestruazione.

## Il ritmo d'insieme
- **Prima metà**: comandano FSH ed estrogeni → crescita ed energia.
- **Metà ciclo**: picco di LH → ovulazione.
- **Seconda metà**: comanda il progesterone → preparazione e, se non c'è gravidanza, mestruazione.

Squilibri di questi ormoni stanno dietro a molte irregolarità: per questo, quando i sintomi sono importanti, è utile parlarne con il ginecologo, eventualmente con esami ormonali mirati.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_temp", ARTICLE, CYCLE, "Fondamenti",
            title = "Temperatura basale: misurarla nel modo giusto",
            subtitle = "Il metodo più attendibile per confermare che l'ovulazione è avvenuta.",
            durationMinutes = 6,
            bodyMarkdown = """
# La temperatura basale (TB)

La **temperatura basale** è la temperatura del corpo a riposo completo, appena sveglia. Dopo l'ovulazione il **progesterone** la fa salire di **0,2–0,5 °C** e la mantiene alta fino alla mestruazione. Tracciarla permette di **confermare che l'ovulazione è avvenuta** (non di prevederla in anticipo).

## Come misurarla bene
1. **Appena sveglia**, prima di alzarti, parlare o bere, dopo almeno **3–4 ore di sonno continuativo**.
2. Sempre con lo **stesso termometro** (meglio un termometro basale a due decimali) e sempre per la **stessa via** (orale, vaginale o rettale).
3. Sempre **alla stessa ora**, il più possibile.
4. Annota subito il valore: anche piccole differenze contano.

## Come leggere la curva
- Nella prima metà del ciclo i valori sono **più bassi**.
- Dopo l'ovulazione si osserva un **rialzo stabile** che dura almeno 3 giorni.
- Il rialzo conferma l'ovulazione **a posteriori**: l'ovulazione è avvenuta poco prima del salto.

## Cosa può alterare la misura
Notte insonne, alcol la sera prima, febbre, alzarsi presto, stress, cambi di orario o di termometro. Per questo conta il **quadro d'insieme** di più giorni, non il singolo valore.

## Consiglio pratico
La temperatura basale dà il meglio se combinata con l'osservazione del **muco cervicale** e, se vuoi, con i **test LH**. Insieme formano il cosiddetto metodo sintotermico, molto più affidabile di un singolo segnale. Registra i valori ogni giorno: l'app costruirà la curva per te.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_tracciare", ARTICLE, CYCLE, "Fondamenti",
            title = "Come tracciare il ciclo (e perché)",
            subtitle = "Cosa annotare ogni giorno per previsioni davvero personalizzate.",
            durationMinutes = 5,
            bodyMarkdown = """
# Tracciare il ciclo: una guida pratica

Tracciare non significa solo segnare quando arriva la mestruazione: significa raccogliere i **segnali del corpo** così che l'app possa imparare il *tuo* ritmo e darti previsioni su misura.

## Cosa annotare ogni giorno
- **Flusso**: presenza e intensità (scarso, medio, abbondante).
- **Muco cervicale**: secco, appiccicoso, cremoso, acquoso, filante.
- **Temperatura basale**, se la misuri.
- **Sintomi**: dolore, mal di testa, gonfiore, tensione al seno.
- **Umore ed energia**.
- **Sonno** e quantità di acqua.
- **Rapporti** e **test LH**, se utili al tuo obiettivo.

## Perché serve costanza
Le previsioni si basano sulla **media** e sulla **variabilità** dei tuoi cicli recenti. Con pochi dati la stima è approssimativa; dopo **2–3 cicli registrati con regolarità** diventa molto più precisa, soprattutto se il tuo ciclo non è perfettamente regolare.

## Come leggere le previsioni
L'app stima la **prossima mestruazione**, la **finestra fertile** e il **giorno di ovulazione** a partire dai tuoi dati. Sono stime statistiche: il corpo può anticipare o ritardare per stress, viaggi, malattia o cambi di abitudini. Usale come bussola, non come certezza assoluta — e mai come metodo contraccettivo.

## Un'abitudine, non un compito
Bastano pochi secondi al giorno. Scegli un momento fisso (per esempio la sera) e rendilo un piccolo rituale di ascolto di te stessa.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_irregolare", ARTICLE, CYCLE, "Fondamenti",
            title = "Ciclo irregolare: quando è normale e quando informarsi",
            subtitle = "Variabilità fisiologica, cause comuni e segnali da non trascurare.",
            durationMinutes = 5,
            bodyMarkdown = """
# Quando un ciclo è davvero irregolare

Una certa variabilità è **normale**: quasi nessun ciclo è identico al precedente. Si parla di ciclo irregolare quando la durata **cambia molto** da un mese all'altro (oltre 7–9 giorni di differenza) oppure esce stabilmente dall'intervallo **21–35 giorni**.

## Cause comuni e spesso temporanee
- **Stress** fisico o emotivo.
- **Variazioni di peso** rapide, in aumento o in calo.
- **Attività fisica molto intensa** o sottoalimentazione.
- **Viaggi** e cambi di fuso.
- Fasi della vita: **adolescenza** (i primi anni dopo il menarca sono spesso irregolari) e **perimenopausa**.

## Cause mediche da indagare
- Disturbi della **tiroide**.
- **Sindrome dell'ovaio policistico (PCOS)**.
- Iperprolattinemia o altri squilibri ormonali.
- Problemi uterini (fibromi, polipi).

## Cosa fare
1. **Registra con costanza**: i dati di più cicli sono lo strumento più utile per il medico.
2. Cura sonno, alimentazione e gestione dello stress.
3. **Parlane con il ginecologo** se l'irregolarità è marcata, se la mestruazione **manca per più di 3 mesi** senza gravidanza, se i cicli sono molto ravvicinati o se compaiono sanguinamenti anomali.

L'irregolarità non è una diagnosi, ma un segnale: spesso si risolve, a volte va approfondita. Tracciare aiuta a capirlo.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_spotting", ARTICLE, CYCLE, "Fondamenti",
            title = "Spotting e perdite intermestruali",
            subtitle = "Cosa sono, quando sono fisiologiche e quando segnalarle.",
            durationMinutes = 4,
            bodyMarkdown = """
# Spotting: piccole perdite tra un ciclo e l'altro

Lo **spotting** è una perdita di sangue **scarsa**, spesso marroncina o rosata, che compare **al di fuori della mestruazione** vera e propria. In molti casi è del tutto benigno.

## Quando può essere fisiologico
- **A metà ciclo**, intorno all'ovulazione, per il calo fisiologico di estrogeni.
- **Nei primi mesi** di uso di un contraccettivo ormonale (pillola, spirale, impianto).
- **All'inizio di una gravidanza** (impianto), come perdita lieve.
- Subito **prima o dopo** la mestruazione.

## Quando annotarlo e parlarne
Segnala sempre nel diario **data, colore e intensità**. Conviene parlarne con il ginecologo se lo spotting è:
- **frequente** o presente in molti cicli;
- **abbondante** o con coaguli;
- **dopo i rapporti**;
- **dopo la menopausa** (in questo caso va sempre valutato);
- accompagnato da **dolore, febbre o perdite maleodoranti**.

## Perché tracciarlo
Registrare le perdite intermestruali aiuta a distinguere un pattern fisiologico da un segnale che merita un controllo, e fornisce al medico un quadro chiaro nel tempo.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_perimenopausa", ARTICLE, CYCLE, "Fondamenti",
            title = "Perimenopausa: il ciclo che cambia",
            subtitle = "Cosa aspettarsi negli anni di transizione verso la menopausa.",
            durationMinutes = 5,
            bodyMarkdown = """
# Perimenopausa: la transizione

La **perimenopausa** è la fase di transizione che precede la menopausa, può durare **diversi anni** e di solito inizia tra i 40 e i 50 anni (a volte prima). Gli ormoni oscillano e il ciclo cambia ritmo.

## Segnali tipici
- Cicli **più corti o più lunghi**, flusso più scarso o più abbondante.
- **Salti** di mestruazione.
- **Vampate di calore** e sudorazioni notturne.
- Disturbi del **sonno** e dell'umore.
- Secchezza vaginale, calo del desiderio.

## La menopausa "vera"
Si parla di menopausa dopo **12 mesi consecutivi senza mestruazioni**. Tutto il periodo prima è perimenopausa: la fertilità si riduce ma **non è zero**, quindi una contraccezione può ancora servire.

## Cosa aiuta
- **Movimento regolare** e una buona alimentazione (calcio, vitamina D, proteine).
- Curare il **sonno** e tecniche di gestione dello stress.
- Tracciare i cicli per riconoscere i cambiamenti.
- **Parlarne con il medico**: esistono opzioni (anche la terapia ormonale, valutata caso per caso) per i sintomi più disturbanti.

## Quando approfondire
Sanguinamenti molto abbondanti, perdite dopo i rapporti o qualsiasi sanguinamento **dopo** la menopausa vanno sempre valutati dal ginecologo.
            """.trimIndent()
        ),

        // ----------------------------- Fertilità -----------------------------
        AcademyContent(
            "cyc_muco", ARTICLE, CYCLE, "Fertilità", phases = listOf("Ovulatoria", "Follicolare"),
            title = "Come leggere il muco cervicale",
            subtitle = "Il segnale biologico più immediato e gratuito della finestra fertile.",
            durationMinutes = 5,
            bodyMarkdown = """
# Il muco cervicale: la bussola della fertilità

Il **muco cervicale** è prodotto dalla cervice e cambia consistenza durante il ciclo, seguendo gli **estrogeni**. Imparare a osservarlo è uno dei modi più semplici ed economici per riconoscere la **finestra fertile**.

## Le tappe nel ciclo
- **Secco / assente** (subito dopo la mestruazione): estrogeni bassi, **bassa fertilità**.
- **Appiccicoso**, denso e biancastro: la fertilità inizia a salire.
- **Cremoso**, simile a lozione: la finestra fertile si avvicina.
- **Acquoso**, più scivoloso e trasparente: **alta fertilità**.
- **Filante e trasparente**, come **albume d'uovo crudo** (si allunga tra le dita): è il segno del **picco di fertilità**, in genere a ridosso dell'ovulazione.

Dopo l'ovulazione, sotto l'effetto del progesterone, il muco torna rapidamente **denso, scarso o assente**.

## Come osservarlo
1. Controlla **ogni giorno**, sempre nello stesso modo (alla carta igienica o a inizio doccia).
2. Valuta **colore, quantità ed elasticità**.
3. **Registralo** nell'app: la sequenza nel tempo vale più del singolo giorno.

## Perché è utile
Il muco "a chiara d'uovo" segnala i giorni di **massima fertilità**: utile sia per **cercare** una gravidanza sia, all'opposto, per riconoscere i giorni a rischio. Da solo non è un metodo contraccettivo affidabile: per quello servono metodi validati e formazione adeguata.

## Quando può ingannare
Infezioni, lubrificanti, liquido seminale, eccitazione e alcuni farmaci possono alterare l'aspetto del muco. In questi casi affidati anche agli altri segnali (temperatura basale, test LH).
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_lh", ARTICLE, CYCLE, "Fertilità", phases = listOf("Ovulatoria"),
            title = "Stick di ovulazione (test LH): guida completa",
            subtitle = "Come e quando usarli per individuare il picco e i giorni più fertili.",
            durationMinutes = 5,
            bodyMarkdown = """
# I test di ovulazione (test LH)

I **test di ovulazione** misurano la concentrazione di **LH** nelle urine. Poiché il **picco di LH** precede l'ovulazione di circa **24–36 ore**, un test positivo indica che l'ovulazione è **imminente**: sono i due giorni più fertili del ciclo.

## Quando iniziare
Dipende dalla durata del tuo ciclo. Una regola pratica: comincia a testare **alcuni giorni prima** dell'ovulazione stimata dall'app. Con cicli irregolari conviene iniziare prima e testare per più giorni.

## Come usarli bene
- Testa **alla stessa ora** ogni giorno (molti preferiscono il primo pomeriggio).
- **Non bere molto** nelle 1–2 ore precedenti: diluiresti l'LH.
- Segui le istruzioni del produttore per i tempi di lettura.

## Come leggerli
- **Positivo**: la linea del test è **uguale o più scura** della linea di controllo → picco di LH, ovulazione vicina.
- **Negativo**: linea più chiara del controllo.

Quando ottieni il primo positivo, i giorni più fertili sono **quel giorno e il successivo**. Registralo: l'app può usare il test LH positivo per **confermare e affinare** la stima dell'ovulazione.

## Limiti da conoscere
- In caso di **PCOS** l'LH può essere alto a tratti e dare falsi positivi.
- Un picco di LH non garantisce al 100% che l'ovulazione avvenga.
- Per questo i test danno il meglio **insieme** a muco cervicale e temperatura basale.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_finestra", ARTICLE, CYCLE, "Fertilità", phases = listOf("Follicolare", "Ovulatoria"),
            title = "Finestra fertile: i 6 giorni che contano",
            subtitle = "Perché la fertilità non è solo il giorno dell'ovulazione.",
            durationMinutes = 4,
            bodyMarkdown = """
# La finestra fertile

Molte persone pensano di poter concepire solo il giorno dell'ovulazione. In realtà la **finestra fertile** è più ampia, perché gli **spermatozoi sopravvivono a lungo**.

## Quanto dura
La finestra comprende circa **6 giorni**: i **5 giorni che precedono** l'ovulazione più il **giorno dell'ovulazione** stessa. Questo perché:
- l'**ovulo** vive solo **12–24 ore** dopo l'ovulazione;
- gli **spermatozoi** possono sopravvivere **fino a 5 giorni** nel muco fertile.

## I giorni più fertili
Le probabilità di concepimento sono massime nei **2–3 giorni che precedono** l'ovulazione e il giorno stesso. Avere rapporti in questa fascia — non solo "il giorno esatto" — aumenta le chance.

## Come individuarla
Combina i segnali:
- **muco a chiara d'uovo** (alta fertilità);
- **test LH positivo** (ovulazione entro 24–36 ore);
- **rialzo della temperatura basale** (conferma che è già avvenuta).

L'app stima la finestra fertile a partire dai tuoi dati. Ricorda: è una **stima statistica**, non una certezza, e per questo **non va usata come metodo contraccettivo**.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_eta_fertilita", ARTICLE, CYCLE, "Fertilità",
            title = "Età, riserva ovarica e fertilità",
            subtitle = "Come cambia la fertilità nel tempo, senza allarmismi.",
            durationMinutes = 4,
            bodyMarkdown = """
# Età e fertilità: i fatti

La fertilità femminile cambia con l'età perché cambia la **riserva ovarica**, cioè il numero e la qualità degli ovociti, presenti in numero finito fin dalla nascita.

## Come evolve
- Fino verso i **30 anni** la fertilità è in genere alta.
- Tra i **30 e i 35** inizia una riduzione graduale.
- **Dopo i 35** il calo accelera; **dopo i 40** è più marcato e aumenta la probabilità di cicli senza ovulazione.

Questi sono **dati medi di popolazione**: ogni persona è diversa e l'età è solo uno dei fattori.

## Cosa influisce oltre all'età
Stile di vita (fumo, alcol, peso), salute ginecologica (endometriosi, PCOS), fattori genetici e anche la **fertilità del partner**.

## Quando approfondire
Se cercate una gravidanza, conviene rivolgersi al medico:
- dopo **12 mesi** di rapporti mirati senza successo se hai **meno di 35 anni**;
- dopo **6 mesi** se ne hai **più di 35**;
- **subito** se ci sono cicli molto irregolari, endometriosi nota o altri fattori di rischio.

## Messaggio chiave
Conoscere questi tempi serve a **decidere con consapevolezza**, non a creare ansia. Tracciare i cicli e parlarne per tempo con uno specialista è il modo migliore per orientarsi.
            """.trimIndent()
        ),

        // ----------------------------- Concepimento -----------------------------
        AcademyContent(
            "cyc_concepimento", ARTICLE, CYCLE, "Concepimento", phases = listOf("Ovulatoria", "Follicolare"),
            title = "Aumentare le probabilità di concepire",
            subtitle = "Tempistiche, abitudini e attese realistiche.",
            durationMinutes = 6,
            bodyMarkdown = """
# Concepimento: come dare una mano alla natura

Concepire è in gran parte una questione di **tempismo** e di **salute generale**. Ecco cosa fa davvero la differenza.

## 1. Centrare la finestra fertile
La fertilità è massima nei **2–3 giorni prima dell'ovulazione** e il giorno stesso. Individua questi giorni con:
- **muco cervicale** trasparente ed elastico;
- **test LH** positivi;
- **temperatura basale** (conferma a posteriori).

Avere rapporti **ogni 1–2 giorni** durante la finestra fertile, senza ossessione per il "giorno esatto", è la strategia più efficace.

## 2. Preparare il corpo (entrambi)
- **Acido folico**: inizia ad assumerlo **prima** del concepimento (di norma 400 µg al giorno, salvo diversa indicazione medica): riduce il rischio di difetti del tubo neurale.
- **Smettere di fumare** e ridurre l'alcol, per entrambi i partner.
- Mantenere un **peso equilibrato** e un'attività fisica moderata.
- Limitare l'eccesso di caffeina.
- Curare il **sonno** e ridurre lo stress.

## 3. Avere aspettative realistiche
Anche con tutto "perfetto", la probabilità di concepire in un singolo ciclo è in media del **15–25%**. La maggior parte delle coppie fertili concepisce **entro un anno**. Non concepire subito è quindi spesso normale.

## Quando chiedere aiuto
Rivolgetevi al medico dopo **12 mesi** di tentativi (o **6 mesi** se la donna ha più di 35 anni), oppure prima in presenza di cicli molto irregolari, endometriosi, o altri fattori noti. La valutazione riguarda **entrambi** i partner.

## Nota importante
L'app aiuta a riconoscere i giorni fertili e a ordinare i dati per il medico, ma **non sostituisce** una consulenza specialistica.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_preconcezionale", ARTICLE, CYCLE, "Concepimento",
            title = "Preparazione preconcezionale e acido folico",
            subtitle = "I passi utili nei mesi prima di cercare una gravidanza.",
            durationMinutes = 4,
            bodyMarkdown = """
# Prepararsi prima del concepimento

I mesi che precedono una gravidanza sono un'occasione preziosa: alcune scelte fatte **prima** del concepimento proteggono la salute del futuro bambino e della madre.

## Acido folico: il punto fermo
L'**acido folico** (vitamina B9) è raccomandato **almeno 1 mese prima** del concepimento e nel primo trimestre. Riduce in modo importante il rischio di **difetti del tubo neurale** (come la spina bifida). Il dosaggio standard è di solito **400 µg al giorno**, ma può essere maggiore in alcune condizioni: segui l'indicazione del medico.

## Altri passi utili
- **Controllo preconcezionale** dal medico o ginecologo.
- Verifica delle **vaccinazioni** (per esempio rosolia) e degli esami di base.
- **Stop al fumo** e all'alcol; revisione di farmaci e integratori con il medico.
- **Alimentazione varia** e peso equilibrato.
- Gestione di eventuali condizioni croniche (tiroide, diabete, ipertensione) **prima** del concepimento.

## Anche il partner conta
La qualità degli spermatozoi migliora con uno stile di vita sano: niente fumo, alcol moderato, peso equilibrato, evitare il calore eccessivo prolungato.

## Tempistica
Iniziare a tracciare il ciclo già in questa fase aiuta a conoscere il proprio ritmo e a individuare con anticipo la finestra fertile quando inizierete i tentativi.
            """.trimIndent()
        ),

        // ----------------------------- Alimentazione -----------------------------
        AcademyContent(
            "cyc_alimentazione", ARTICLE, CYCLE, "Alimentazione",
            title = "Alimentazione fase per fase",
            subtitle = "Adattare i pasti all'energia e ai bisogni di ogni fase del ciclo.",
            durationMinutes = 6,
            bodyMarkdown = """
# Mangiare seguendo il ciclo

Non serve una dieta rigida: bastano piccoli accorgimenti per **assecondare** i bisogni di ogni fase. La base resta sempre un'alimentazione **varia, regolare e completa**.

## Fase mestruale
Con il flusso si perde **ferro**: privilegia legumi, verdure a foglia verde, carni magre (se le mangi) e abbina **vitamina C** (agrumi, kiwi, peperoni) che ne migliora l'assorbimento. Punta su **idratazione** e cibi caldi e confortanti; tisane allo zenzero possono aiutare contro i crampi.

## Fase follicolare
L'energia risale: ottimo momento per **proteine** di qualità, cereali integrali e tanta **frutta e verdura fresca**. Sostengono la crescita follicolare e la lucidità mentale.

## Fase ovulatoria
Punta su **fibre** e **antiossidanti** (verdure colorate, frutti di bosco) e su tanta **acqua**. Pasti leggeri e freschi accompagnano bene il picco di energia.

## Fase luteale
È la fase dei sintomi premestruali. Aiutano:
- **Carboidrati complessi** (avena, legumi, cereali integrali) per stabilizzare l'umore e contenere le voglie di dolci.
- **Magnesio** (frutta secca, cioccolato fondente, verdure verdi) per tensione e crampi.
- **Ridurre sale** (contro la ritenzione), **zuccheri raffinati** e **caffeina**.

## Principi che valgono sempre
- Pasti **regolari**: saltare i pasti peggiora stanchezza e voglie.
- Abbastanza **proteine, fibre e grassi buoni** a ogni pasto.
- **Idratazione** costante.

Non esistono cibi "magici": la costanza di buone abitudini conta più del singolo alimento.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_ferro", ARTICLE, CYCLE, "Alimentazione", phases = listOf("Mestruale"),
            title = "Ferro e anemia: attenzione al flusso abbondante",
            subtitle = "Riconoscere la carenza di ferro e come prevenirla.",
            durationMinutes = 4,
            bodyMarkdown = """
# Ferro e ciclo mestruale

Con ogni mestruazione si perde una piccola quantità di **ferro**. Se il flusso è **abbondante** o prolungato, nel tempo può svilupparsi una **carenza di ferro**, fino all'**anemia**.

## Segnali di carenza
- **Stanchezza** marcata e persistente.
- **Pallore**, fiato corto sotto sforzo, palpitazioni.
- Mal di testa, capogiri, difficoltà di concentrazione.
- Unghie fragili, caduta di capelli.

## Come fare il pieno con l'alimentazione
- **Fonti di ferro**: legumi, verdure a foglia verde scuro, frutta secca, cereali integrali; carne e pesce se li mangi.
- Abbina sempre la **vitamina C** (agrumi, kiwi, peperoni, pomodori): aumenta molto l'assorbimento del ferro vegetale.
- **Tè e caffè** durante i pasti **riducono** l'assorbimento: meglio lontano dai pasti principali.

## Quando rivolgersi al medico
Se hai sintomi di anemia, o se il flusso è così abbondante da costringerti a **cambiare assorbente ogni 1–2 ore**, da contenere **coaguli grandi** o da durare oltre **7 giorni**, parlane con il medico: con un semplice esame del sangue si valuta il ferro e, se serve, si imposta un'**integrazione** (da non assumere di iniziativa, perché un eccesso è dannoso).

Tracciare l'intensità del flusso aiuta a fornire al medico un quadro preciso.
            """.trimIndent()
        ),

        // ----------------------------- Benessere -----------------------------
        AcademyContent(
            "cyc_dismenorrea", ARTICLE, CYCLE, "Benessere", phases = listOf("Mestruale"),
            title = "Gestire i dolori mestruali (dismenorrea)",
            subtitle = "Strategie pratiche per i crampi e quando non vanno sottovalutati.",
            durationMinutes = 5,
            bodyMarkdown = """
# Sollievo dai dolori mestruali

I **crampi mestruali** (dismenorrea) nascono dalle contrazioni dell'utero, stimolate da sostanze chiamate **prostaglandine**, che servono a far sfaldare l'endometrio. Un certo fastidio è comune; il dolore **molto intenso o invalidante**, invece, non va considerato "normale".

## Rimedi non farmacologici
- **Calore** sul basso ventre (borsa dell'acqua calda, fascia termica): rilassa la muscolatura.
- **Movimento dolce**: camminate, stretching, yoga. Sembra controintuitivo, ma aiuta.
- **Respirazione** e tecniche di rilassamento.
- **Idratazione** e pasti regolari; ridurre sale e caffeina nei giorni critici.
- Un sonno adeguato.

## Farmaci
Gli **antinfiammatori (FANS)** sono efficaci se presi **all'inizio** del dolore (o poco prima, se il pattern è prevedibile), perché agiscono sulle prostaglandine. Vanno usati **secondo le indicazioni** del medico o del farmacista, rispettando dosi e controindicazioni.

## Quando parlarne con il ginecologo
Non rimandare se il dolore:
- è **intenso** e ti impedisce le normali attività;
- **peggiora nel tempo**;
- non risponde ai comuni rimedi;
- si accompagna a dolore durante i rapporti, sanguinamenti anomali o difficoltà a concepire.

Un dolore così può segnalare condizioni come l'**endometriosi**, che vanno indagate. Tenere un diario del dolore (intensità, durata, giorni) è preziosissimo per il medico.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_pms", ARTICLE, CYCLE, "Benessere", phases = listOf("Luteale"),
            title = "Sindrome premestruale (PMS): cosa aiuta davvero",
            subtitle = "Umore, gonfiore e voglie nella fase luteale.",
            durationMinutes = 5,
            bodyMarkdown = """
# La sindrome premestruale (PMS)

Nella **fase luteale**, dopo l'ovulazione, le oscillazioni di estrogeni e progesterone possono dare un insieme di sintomi fisici ed emotivi noto come **sindrome premestruale**. Compaiono nei giorni che precedono la mestruazione e **scompaiono** con il suo arrivo.

## Sintomi tipici
- **Fisici**: gonfiore addominale, ritenzione idrica, tensione al seno, mal di testa, stanchezza, voglia di dolci.
- **Emotivi**: irritabilità, sbalzi d'umore, ansia, malinconia, minore tolleranza allo stress.

## Strategie efficaci
- **Alimentazione**: meno sale, zuccheri raffinati, alcol e caffeina; più carboidrati complessi, verdura e cibi ricchi di **magnesio**.
- **Movimento regolare**: l'attività fisica migliora umore e gonfiore.
- **Sonno** curato e routine stabili.
- **Magnesio e vitamina B6** possono aiutare alcune persone (da valutare con il medico).
- **Tracciare i sintomi**: riconoscere il pattern aiuta a prepararsi e a distinguere la PMS da altro.

## Quando è qualcosa di più
Se i sintomi **emotivi** sono gravi — rabbia intensa, tristezza profonda, ansia che compromette relazioni e lavoro — potrebbe trattarsi di **disturbo disforico premestruale (PMDD)**, una forma più severa che merita una valutazione medica dedicata.

La PMS è comune e gestibile: l'obiettivo è ridurne l'impatto, non "sopportarla" e basta.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_pmdd", ARTICLE, CYCLE, "Benessere", phases = listOf("Luteale"),
            title = "Disturbo disforico premestruale (PMDD)",
            subtitle = "Quando i sintomi emotivi premestruali diventano severi.",
            durationMinutes = 4,
            bodyMarkdown = """
# Quando la PMS diventa PMDD

Il **disturbo disforico premestruale (PMDD)** è una forma **severa** di sindrome premestruale, in cui prevalgono sintomi **emotivi intensi** capaci di interferire con relazioni, lavoro e vita quotidiana. Non è "esagerare": è una condizione riconosciuta, legata a una particolare sensibilità alle normali oscillazioni ormonali.

## Segnali che vanno oltre la PMS comune
- **Tristezza profonda** o senso di disperazione.
- **Irritabilità o rabbia** marcate, conflitti relazionali ricorrenti.
- **Ansia** o tensione forte.
- Sensazione di **perdita di controllo**, difficoltà di concentrazione.
- Comparsa regolare nella **fase luteale** e **scomparsa** con l'arrivo della mestruazione.

## Come si riconosce
La chiave è la **ciclicità**: i sintomi tornano in modo ricorrente nella seconda metà del ciclo e si risolvono con la mestruazione. **Tracciare per almeno due cicli** umore e sintomi è il primo passo, e fornisce dati preziosi al medico.

## Cosa fare
Il PMDD si può trattare. Le opzioni — da valutare **con un professionista** — includono interventi sullo stile di vita, alcuni approcci nutrizionali, supporto psicologico e, nei casi indicati, terapie mediche specifiche. Se ti riconosci in questa descrizione, **parlane**: non è qualcosa da affrontare da sole.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_sport", ARTICLE, CYCLE, "Benessere", phases = listOf("Follicolare", "Ovulatoria"),
            title = "Sport e movimento durante il ciclo",
            subtitle = "Allenarsi assecondando l'energia delle diverse fasi.",
            durationMinutes = 4,
            bodyMarkdown = """
# Allenarsi seguendo il ciclo

Il movimento fa bene in **tutte** le fasi del ciclo: migliora umore, sonno, dolori e gestione dello stress. Quello che può cambiare è l'**intensità** che ti senti di sostenere.

## Fase follicolare e ovulatoria
Con estrogeni in salita, **energia e forza** sono al massimo. È il momento ideale per:
- allenamenti di **forza** e ad **alta intensità**;
- nuovi obiettivi e carichi più impegnativi;
- sport di gruppo e sfide.

Attenzione, intorno all'ovulazione, alla maggiore lassità dei legamenti: cura il riscaldamento.

## Fase luteale e mestruale
L'energia può calare e i sintomi premestruali farsi sentire. Vanno benissimo:
- **camminate**, nuoto leggero, bicicletta tranquilla;
- **yoga, pilates, mobilità e stretching**;
- allenamenti più brevi, con più recupero.

## Durante la mestruazione
Nessuna fase **vieta** il movimento. Anzi, un'attività dolce spesso **allevia i crampi**. Ascolta il corpo: se sei molto stanca, riposa senza sensi di colpa.

## La regola d'oro
Non esiste un programma uguale per tutte: **traccia energia e prestazioni** insieme alle fasi e impara a riconoscere il *tuo* ritmo. La costanza nel tempo conta più della singola sessione perfetta.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_sonno", ARTICLE, CYCLE, "Benessere", phases = listOf("Luteale"),
            title = "Sonno e ciclo mestruale",
            subtitle = "Perché il riposo cambia nelle diverse fasi e come dormire meglio.",
            durationMinutes = 4,
            bodyMarkdown = """
# Sonno e ormoni

La qualità del sonno **oscilla** durante il ciclo. Nella **fase luteale** e nei giorni premestruali molte persone dormono peggio: la **temperatura corporea** è più alta, il progesterone influenza il riposo e i sintomi della PMS (ansia, gonfiore, crampi) disturbano il sonno.

## Cosa succede fase per fase
- **Follicolare**: sonno spesso più stabile e ristoratore.
- **Ovulatoria**: in genere buono, con energia diurna alta.
- **Luteale**: possibile più fatica ad addormentarsi e risvegli.
- **Mestruale**: crampi e flusso possono interrompere il sonno nei primi giorni.

## Consigli per dormire meglio
- **Orari regolari**, anche nel weekend.
- **Camera fresca** e buia: utile soprattutto in fase luteale, quando la temperatura è più alta.
- **Meno schermi** nell'ora prima di dormire.
- **Caffeina** solo al mattino; alcol limitato la sera.
- Routine rilassante: respirazione, lettura, stretching leggero.
- Per i crampi notturni: **calore** e, se serve, un antinfiammatorio secondo indicazione.

## Tracciare aiuta
Annotare la **qualità del sonno** insieme alle fasi rende evidenti i pattern: così puoi anticipare i periodi difficili e proteggere il riposo quando serve di più.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_stress", ARTICLE, CYCLE, "Benessere",
            title = "Stress, cortisolo e regolarità del ciclo",
            subtitle = "Come la mente influenza gli ormoni (e viceversa).",
            durationMinutes = 4,
            bodyMarkdown = """
# Stress e ciclo: un legame a doppio senso

Lo **stress** non è solo una sensazione: attiva ormoni come il **cortisolo** che, se cronicamente elevati, possono **interferire** con i segnali che regolano l'ovulazione. Ecco perché in periodi intensi il ciclo può **ritardare, accorciarsi o saltare**.

## Cosa può succedere
- **Ovulazione ritardata** o assente in quel ciclo → mestruazione che arriva più tardi.
- Cicli **più irregolari** in periodi di stress prolungato.
- Sintomi premestruali **più marcati**.

## A doppio senso
Funziona anche al contrario: i sintomi del ciclo (dolore, PMS, insonnia) possono aumentare lo stress percepito. Spezzare il circolo aiuta entrambi.

## Strategie utili
- **Movimento regolare** ma non estremo (l'eccesso è esso stesso uno stress per il corpo).
- **Sonno** sufficiente e costante.
- Tecniche di **rilassamento**: respirazione, meditazione, tempo all'aria aperta.
- Alimentazione regolare: **non saltare i pasti**.
- Ritagliare momenti di pausa reali, non solo "a parole".

## Quando approfondire
Se il ciclo manca per **più di 3 mesi** in assenza di gravidanza, o se l'irregolarità è marcata e persistente, parlane con il medico: lo stress è una causa frequente ma non l'unica, e va distinto da altre condizioni.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_intima", ARTICLE, CYCLE, "Benessere",
            title = "Salute intima e microbiota vaginale",
            subtitle = "Igiene corretta e segnali di infezione da non ignorare.",
            durationMinutes = 4,
            bodyMarkdown = """
# Prendersi cura della salute intima

L'ambiente vaginale è un ecosistema delicato, popolato da batteri "buoni" (soprattutto **lattobacilli**) che mantengono un pH acido e proteggono dalle infezioni. L'obiettivo dell'igiene è **rispettare** questo equilibrio, non stravolgerlo.

## Igiene corretta
- **Detergenti delicati**, a pH adeguato, solo per la parte esterna.
- **Niente lavande interne**: alterano il microbiota e aumentano il rischio di infezioni.
- Biancheria in **cotone**, evitare indumenti troppo stretti a lungo.
- Cambiare con regolarità assorbenti e proteggi-slip durante il flusso.

## Cosa è normale
Le **perdite** cambiano durante il ciclo (vedi il muco cervicale): da scarse e dense a trasparenti ed elastiche. Variano per consistenza e quantità: è fisiologico.

## Segnali di infezione
Conviene farsi vedere dal medico in caso di:
- **prurito o bruciore** intensi;
- perdite **maleodoranti**, giallo-verdastre o "a ricotta";
- **dolore** durante i rapporti o nella minzione;
- arrossamento e gonfiore.

Le infezioni più comuni (candida, vaginosi batterica) sono frequenti e curabili: l'autodiagnosi e l'autocura ripetuta, però, possono peggiorare le cose. Meglio una valutazione mirata.

## Buone abitudini extra
Urinare dopo i rapporti, asciugarsi da davanti verso dietro e una buona idratazione completano la cura quotidiana.
            """.trimIndent()
        ),

        // ----------------------------- Disturbi -----------------------------
        AcademyContent(
            "cyc_endometriosi", ARTICLE, CYCLE, "Disturbi",
            title = "Endometriosi: riconoscerla per tempo",
            subtitle = "Quando il dolore mestruale non è 'normale'.",
            durationMinutes = 5,
            bodyMarkdown = """
# Endometriosi

L'**endometriosi** è una malattia cronica in cui un tessuto **simile all'endometrio** (il rivestimento interno dell'utero) cresce **fuori dall'utero**: su ovaie, tube, peritoneo e altri organi pelvici. Ad ogni ciclo questo tessuto risponde agli ormoni, sanguina e provoca **infiammazione, dolore e aderenze**.

## Sintomi che devono far pensare
- **Dolore mestruale intenso**, che peggiora nel tempo e non si calma con i comuni rimedi.
- **Dolore pelvico cronico**, anche fuori dalla mestruazione.
- **Dolore durante o dopo i rapporti**.
- Dolore alla minzione o all'evacuazione, soprattutto durante il ciclo.
- **Stanchezza** marcata.
- Difficoltà a concepire (a volte è il primo segnale).

L'intensità dei sintomi **non** è proporzionale all'estensione della malattia: si può soffrire molto con forme lievi e viceversa.

## Perché la diagnosi è spesso tardiva
Il dolore mestruale viene a lungo considerato "normale", così tra i primi sintomi e la diagnosi possono passare anni. Per questo è importante **non minimizzare** un dolore che condiziona la vita.

## Cosa fare
- **Tieni un diario** di dolore, sintomi e giorni: è un aiuto concreto per il medico.
- **Parlane con il ginecologo** se ti riconosci nei sintomi.
- La gestione è personalizzata e può includere terapie del dolore, terapie ormonali e, in casi selezionati, la chirurgia.

La diagnosi precoce migliora la qualità di vita e tutela la fertilità: ascoltare il proprio dolore è il primo passo.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_pcos", ARTICLE, CYCLE, "Disturbi",
            title = "Sindrome dell'ovaio policistico (PCOS)",
            subtitle = "Cicli irregolari, ormoni, metabolismo e fertilità.",
            durationMinutes = 5,
            bodyMarkdown = """
# Sindrome dell'ovaio policistico (PCOS)

La **PCOS** è uno dei disturbi ormonali più comuni in età fertile. Nasce da uno **squilibrio ormonale** che ostacola la regolare ovulazione e spesso si associa a una particolare sensibilità all'insulina.

## Come si riconosce
La diagnosi (di competenza medica) si basa sulla presenza di almeno due tra:
- **Cicli irregolari o assenti** (ovulazione rara o assente).
- Segni di **eccesso di androgeni**: acne, aumento di peli (irsutismo), a volte caduta di capelli.
- **Ovaie con aspetto policistico** all'ecografia.

## Altri aspetti frequenti
- Difficoltà a **concepire** (per la mancata ovulazione).
- Tendenza all'**aumento di peso** e all'insulino-resistenza.
- Maggiore rischio, nel tempo, di alterazioni metaboliche.

## Cosa aiuta
- **Stile di vita**: alimentazione equilibrata e movimento regolare migliorano molto i sintomi, anche con piccoli cambiamenti di peso.
- **Percorso medico** con ginecologo ed eventualmente endocrinologo: la gestione è personalizzata e dipende dall'obiettivo (regolarità del ciclo, pelle, fertilità, metabolismo).
- **Tracciare i cicli**: con la PCOS l'ovulazione è imprevedibile, quindi i dati nel tempo sono particolarmente utili (ricorda che i test LH possono dare falsi positivi).

## Messaggio chiave
La PCOS è gestibile e **non impedisce** necessariamente una gravidanza. Una diagnosi corretta e un percorso su misura fanno una grande differenza sulla qualità di vita.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_amenorrea", ARTICLE, CYCLE, "Disturbi",
            title = "Amenorrea: quando la mestruazione manca",
            subtitle = "Le cause dell'assenza di ciclo e quando preoccuparsi.",
            durationMinutes = 4,
            bodyMarkdown = """
# Amenorrea: l'assenza di mestruazioni

Si parla di **amenorrea** quando la mestruazione **manca**. È **primaria** se il primo ciclo non è ancora comparso entro l'età attesa, **secondaria** se mancano le mestruazioni per **almeno 3 mesi** in chi prima le aveva (esclusa la gravidanza).

## Prima di tutto
La causa più comune di mestruazione assente in età fertile è la **gravidanza**: va sempre considerata per prima.

## Altre cause frequenti
- **Stress** intenso o prolungato.
- **Perdita di peso** marcata, disturbi alimentari, **attività fisica estrema** (amenorrea ipotalamica).
- **PCOS** e altri squilibri ormonali.
- Disturbi della **tiroide** o iperprolattinemia.
- **Allattamento** e **perimenopausa** (fisiologiche).
- Alcuni contraccettivi ormonali riducono o eliminano il flusso (in modo atteso).

## Perché non va ignorata
Un'assenza prolungata di ciclo può riflettere uno squilibrio ormonale che, nel tempo, incide anche sulla **salute delle ossa** e sulla fertilità. Non è solo una questione di "comodità".

## Cosa fare
- Escludi una gravidanza.
- **Annota** da quando manca il ciclo e gli eventuali fattori (stress, dieta, allenamento, farmaci).
- **Consulta il medico** se la mestruazione manca da 3 mesi o più: con pochi esami si individua la causa e si imposta il percorso adatto.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_menorragia", ARTICLE, CYCLE, "Disturbi", phases = listOf("Mestruale"),
            title = "Flusso molto abbondante (menorragia)",
            subtitle = "Quando il sanguinamento è eccessivo e va valutato.",
            durationMinutes = 4,
            bodyMarkdown = """
# Quando il flusso è troppo

Il **flusso mestruale abbondante** (menorragia) non è solo un fastidio: se importante e ripetuto può portare a **carenza di ferro** e anemia, e a volte segnala una condizione da trattare.

## Segnali di flusso eccessivo
- Necessità di **cambiare assorbente ogni 1–2 ore** per più ore.
- **Coaguli grandi** (più di una moneta da pochi centimetri).
- Mestruazioni che durano **oltre 7 giorni**.
- Doversi alzare di notte per cambiare la protezione.
- **Stanchezza** e segni di anemia.

## Possibili cause
- **Fibromi** o **polipi** uterini.
- Squilibri ormonali (anche in adolescenza e perimenopausa).
- Disturbi della coagulazione.
- Alcune spirali (IUD) al rame possono aumentare il flusso.
- Disfunzioni tiroidee.

## Cosa fare
- **Traccia l'intensità** del flusso e la durata: dati oggettivi aiutano il medico.
- Cura l'apporto di **ferro** (vedi l'articolo dedicato).
- **Rivolgiti al ginecologo** se riconosci i segnali sopra: esistono trattamenti efficaci, sia per la causa sia per ridurre il flusso e prevenire l'anemia.

Un flusso molto abbondante è comune ma **non va dato per scontato**: merita una valutazione.
            """.trimIndent()
        ),

        // ----------------------------- Contraccezione -----------------------------
        AcademyContent(
            "cyc_contraccezione", ARTICLE, CYCLE, "Contraccezione",
            title = "Metodi contraccettivi: panoramica completa",
            subtitle = "Le opzioni principali, come funzionano e cosa valutare.",
            durationMinutes = 6,
            bodyMarkdown = """
# Panoramica dei metodi contraccettivi

Esistono molti metodi, con efficacia, praticità e caratteristiche diverse. La scelta è **personale** e va fatta con un professionista, in base a salute, stile di vita e progetti.

## Metodi di barriera
- **Preservativo** (esterno o interno): unico metodo che protegge anche dalle **infezioni sessualmente trasmissibili (IST)**. Efficacia legata all'uso corretto e costante.
- **Diaframma** (con gel spermicida): meno diffuso, richiede misura e addestramento.

## Metodi ormonali
Rilasciano ormoni che bloccano l'ovulazione e/o rendono l'ambiente sfavorevole agli spermatozoi:
- **Pillola** (combinata o solo progestinico).
- **Anello vaginale** e **cerotto**.
- **Impianto sottocutaneo** (pluriennale).
- **Spirale ormonale (IUS)**.

Hanno efficacia elevata se usati correttamente; quelli "a lunga durata" (impianto, spirali) tolgono il rischio di dimenticanze.

## Dispositivi intrauterini (IUD)
- **Spirale al rame**: senza ormoni, efficace per anni.
- **Spirale ormonale**: spesso riduce anche il flusso.

## Metodi basati sulla consapevolezza della fertilità
Si basano sull'osservazione di **muco, temperatura e calendario** per individuare i giorni fertili ed evitarli. Possono funzionare **solo** con formazione adeguata, grande costanza e cicli sufficientemente regolari; il margine di errore è più alto.

## Contraccezione d'emergenza
La "pillola del giorno dopo" serve **dopo** un rapporto non protetto o un fallimento del metodo: è tanto più efficace quanto prima si assume. **Non** è un metodo da usare abitualmente.

## Nota importante
> Mensis **non è un metodo contraccettivo**. Le previsioni di fertilità sono stime statistiche e **non** vanno usate per evitare una gravidanza. Per scegliere il metodo giusto, parla con il tuo medico o un consultorio.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_pillola", ARTICLE, CYCLE, "Contraccezione",
            title = "La pillola anticoncezionale: come funziona",
            subtitle = "Tipi, assunzione e cosa fare in caso di dimenticanza.",
            durationMinutes = 4,
            bodyMarkdown = """
# La pillola anticoncezionale

La **pillola** è un contraccettivo ormonale tra i più diffusi. Agisce soprattutto **bloccando l'ovulazione** e modificando muco cervicale ed endometrio.

## Due grandi famiglie
- **Combinata** (estrogeno + progestinico): la più comune; spesso regola il ciclo e riduce dolore e flusso.
- **Solo progestinico** (minipillola): utile quando l'estrogeno è sconsigliato (per esempio durante l'allattamento o in presenza di certi fattori di rischio).

## Come si assume
Si prende **ogni giorno**, possibilmente alla **stessa ora**. A seconda del tipo c'è una pausa o un placebo, durante cui compare un sanguinamento da sospensione (che non è una "vera" mestruazione). La **regolarità** è ciò che rende la pillola efficace.

## Se dimentichi una compressa
Le regole variano in base al tipo di pillola e a quante ore di ritardo. In generale: prendila appena te ne accorgi e **leggi il foglietto illustrativo**; in caso di dubbio, usa anche il **preservativo** nei giorni successivi e chiedi al medico o al farmacista. Vomito o diarrea entro poche ore possono ridurne l'assorbimento.

## Cosa sapere
- Non protegge dalle **IST**: per quello serve il preservativo.
- Può avere benefici extra (acne, dolore, flusso) ma anche controindicazioni: la scelta è **sempre medica**.
- Alcuni farmaci possono ridurne l'efficacia: segnala sempre cosa assumi.

L'app può ricordarti l'assunzione, ma non sostituisce le indicazioni del medico.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_naturali", ARTICLE, CYCLE, "Contraccezione",
            title = "Metodi naturali e consapevolezza della fertilità",
            subtitle = "Come funzionano, pregi e limiti del metodo sintotermico.",
            durationMinutes = 4,
            bodyMarkdown = """
# Conoscere la fertilità: i metodi naturali

I **metodi basati sulla consapevolezza della fertilità** (FAM) usano i segnali del corpo per individuare i **giorni fertili**. Possono servire sia per **cercare** una gravidanza sia per **evitarla**, ma in quest'ultimo caso richiedono rigore e formazione.

## I segnali utilizzati
- **Temperatura basale**: conferma che l'ovulazione è avvenuta (rialzo stabile).
- **Muco cervicale**: indica l'avvicinarsi dell'ovulazione.
- **Calendario**: stima statistica basata sui cicli passati.

Il metodo **sintotermico** combina temperatura e muco (più affidabile dei singoli segnali) e identifica una fase fertile da evitare se non si cerca una gravidanza.

## Pregi
- Senza ormoni né dispositivi.
- Aumentano la **conoscenza del proprio corpo**.
- Utili anche per la ricerca di gravidanza.

## Limiti importanti
- Richiedono **formazione adeguata**, costanza quotidiana e cicli sufficientemente regolari.
- Sono **meno tolleranti agli errori** rispetto ai metodi a lunga durata.
- Stress, malattia, viaggi e turni alterano i segnali.
- **Non proteggono dalle IST**.

## In pratica
Un'app che traccia muco e temperatura è uno **strumento di supporto**, ma da sola **non è un contraccettivo**. Se vuoi usare un metodo naturale per evitare una gravidanza, fatti seguire da un consultorio o da un'esperta formata.
            """.trimIndent()
        ),

        // ----------------------------- Approfondimenti aggiunti -----------------------------
        AcademyContent(
            "cyc_impianto", ARTICLE, CYCLE, "Concepimento", phases = listOf("Luteale"),
            title = "Sanguinamento da impianto o mestruazione?",
            subtitle = "Come distinguere una perdita d'impianto dall'arrivo del ciclo.",
            durationMinutes = 4,
            bodyMarkdown = """
# Perdita da impianto o mestruazione?

Quando l'ovulo fecondato si annida nell'endometrio, alcune persone notano una piccola perdita: il cosiddetto **sanguinamento da impianto**. Si verifica in genere **6–12 giorni dopo l'ovulazione**, quindi pochi giorni prima della data attesa del ciclo, e può creare confusione.

## Come si presenta la perdita da impianto
- **Colore**: spesso rosato o marroncino, raramente rosso vivo.
- **Quantità**: molto **scarsa**, a tratti, non aumenta come una mestruazione.
- **Durata**: poche ore fino a 1–2 giorni.
- **Senza coaguli** e senza il classico crescendo del flusso.

## Come si presenta la mestruazione
- Inizia magari leggera ma **aumenta** nei primi 1–2 giorni.
- Rosso più vivo, può avere coaguli.
- Dura in media 3–7 giorni.

## Non è un test
La perdita da impianto **non compare sempre** e, da sola, **non conferma** una gravidanza. Allo stesso modo, la sua assenza non la esclude. L'unico modo per chiarire è il **test di gravidanza**, attendibile dal giorno previsto della mestruazione (o pochi giorni dopo, per maggiore sicurezza).

## Quando sentire il medico
Se hai perdite anomale ripetute, dolore importante o un test positivo con sanguinamento, parlane con il ginecologo.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_test_gravidanza", ARTICLE, CYCLE, "Concepimento",
            title = "Test di gravidanza: quando farlo e come leggerlo",
            subtitle = "Tempistiche giuste per un risultato affidabile.",
            durationMinutes = 4,
            bodyMarkdown = """
# Il test di gravidanza

I test di gravidanza rilevano l'ormone **beta-hCG**, prodotto dopo l'impianto. Capire **quando** farli evita falsi negativi e ansie inutili.

## Quando farlo
- Il momento più affidabile è **dal primo giorno di ritardo** della mestruazione.
- Farlo troppo presto può dare un **falso negativo**: l'hCG potrebbe non essere ancora abbastanza alto.
- Se hai cicli irregolari, conta almeno **3 settimane dal rapporto** potenzialmente fecondo.

## Come farlo bene
- Usa preferibilmente la **prima urina del mattino**, più concentrata.
- Segui i tempi di lettura indicati: leggere troppo tardi può creare linee "fantasma".

## Come leggerlo
- **Positivo**: anche una linea debole, se compare nei tempi, di solito indica gravidanza.
- **Negativo con ritardo persistente**: ripeti dopo 2–3 giorni; se il ritardo continua, senti il medico.

## Conferma
Il test urinario è molto attendibile, ma la conferma e la datazione si fanno con la **visita** e, se serve, il **dosaggio del beta-hCG nel sangue** e l'ecografia.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_emicrania", ARTICLE, CYCLE, "Benessere", phases = listOf("Mestruale", "Luteale"),
            title = "Emicrania mestruale",
            subtitle = "Quando il mal di testa segue il ritmo del ciclo.",
            durationMinutes = 4,
            bodyMarkdown = """
# L'emicrania mestruale

Alcune persone soffrono di **emicrania legata al ciclo**, scatenata dal **calo degli estrogeni** poco prima della mestruazione. Tende a comparire nei giorni che precedono o accompagnano il flusso.

## Come riconoscerla
- Mal di testa **pulsante**, spesso da un lato.
- Può associarsi a **nausea**, fastidio a luci e suoni.
- Compare in modo **ricorrente** nella stessa fase del ciclo.

## Cosa può aiutare
- **Tracciare** gli attacchi insieme al ciclo per riconoscere il pattern e anticiparli.
- Sonno regolare, idratazione, pasti senza lunghi digiuni.
- Ridurre i fattori scatenanti personali (alcol, stress, sbalzi di caffeina).
- Gli **antidolorifici/antiemicranici** sono più efficaci se presi all'inizio: usali secondo l'indicazione del medico.

## Quando approfondire
Se gli attacchi sono frequenti, intensi o cambiano carattere, parlane con il medico: esistono strategie di **prevenzione** specifiche. Segnala sempre eventuali emicranie con "aura", perché possono influenzare la scelta di alcuni contraccettivi.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_pelle", ARTICLE, CYCLE, "Benessere", phases = listOf("Luteale", "Mestruale"),
            title = "Pelle e acne legate al ciclo",
            subtitle = "Perché la pelle cambia nelle diverse fasi.",
            durationMinutes = 3,
            bodyMarkdown = """
# La pelle che segue il ciclo

La pelle risponde alle oscillazioni ormonali, quindi cambia durante il mese.

## Cosa succede fase per fase
- **Follicolare/ovulatoria**: con gli estrogeni alti la pelle è spesso più **luminosa e idratata**.
- **Luteale/premestruale**: il progesterone e gli androgeni possono aumentare la **produzione di sebo**, favorendo punti neri e brufoli, tipicamente su mento e mandibola.

## Come gestirla
- **Routine costante e delicata**: detersione due volte al giorno, niente prodotti aggressivi.
- Non schiacciare i brufoli (peggiora infiammazione e segni).
- Idratazione e protezione solare anche nei giorni "no".
- Annotare i picchi di acne insieme al ciclo aiuta a prevedere e gestire.

## Quando vedere un dermatologo
Se l'acne è importante, persistente o lascia cicatrici, oppure è associata a peli in eccesso e cicli irregolari (possibile **PCOS**), conviene una valutazione specialistica.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_igiene", ARTICLE, CYCLE, "Benessere", phases = listOf("Mestruale"),
            title = "Assorbenti, coppetta e slip mestruali",
            subtitle = "Pro e contro dei diversi presidi per il flusso.",
            durationMinutes = 4,
            bodyMarkdown = """
# Gestire il flusso: le opzioni

Non esiste un presidio "migliore" in assoluto: dipende da flusso, stile di vita e comfort personale. Conoscerli aiuta a scegliere.

## Assorbenti esterni
Pratici e diffusi. Vanno **cambiati regolarmente** (ogni 4–6 ore, più spesso con flusso abbondante) per igiene e comfort.

## Tamponi interni
Comodi per sport e nuoto. Importante usare l'**assorbenza minima** necessaria e cambiarli ogni 4–8 ore: non superare le ore indicate, per ridurre il rischio (raro) di sindrome da shock tossico.

## Coppetta mestruale
In silicone medico, **riutilizzabile** per anni: ecologica ed economica nel tempo. Si svuota ogni 4–12 ore secondo il flusso, si lava e si sterilizza tra un ciclo e l'altro. Richiede un po' di pratica all'inizio.

## Slip mestruali
Mutandine assorbenti **lavabili**, comode da sole (flusso leggero) o come supporto. Sostenibili e discrete.

## In sintesi
- **Flusso abbondante**: coppetta o assorbenti ad alta assorbenza, cambi frequenti.
- **Sport/mare**: tampone o coppetta.
- **Notte/sicurezza extra**: slip mestruali come supporto.

Qualunque sia la scelta, conta l'**igiene** e il cambio regolare.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_voglie", ARTICLE, CYCLE, "Alimentazione", phases = listOf("Luteale"),
            title = "Voglie e fame premestruale",
            subtitle = "Perché vengono e come gestirle senza sensi di colpa.",
            durationMinutes = 3,
            bodyMarkdown = """
# Le voglie premestruali

Nella fase luteale è comune avere più **fame** e voglia di dolci o cibi salati. Non è mancanza di volontà: c'entrano gli ormoni e un lieve aumento del dispendio energetico.

## Perché succedono
- Le oscillazioni di **serotonina** spingono verso i carboidrati, che ne favoriscono la produzione (e migliorano l'umore).
- Cali di zuccheri e pasti irregolari accentuano le voglie.

## Come gestirle
- **Non saltare i pasti**: la fame "arretrata" peggiora le voglie.
- Scegli **carboidrati complessi** (avena, legumi, integrali): saziano più a lungo.
- Abbina **proteine e fibre** a ogni pasto.
- Concediti il dolce in **porzioni ragionevoli**, senza demonizzarlo: la rigidità spesso porta agli eccessi.
- Cura **sonno** e idratazione: la stanchezza aumenta la ricerca di zuccheri.

## Messaggio chiave
Avere più appetito prima del ciclo è **fisiologico**. L'obiettivo non è reprimere, ma assecondare il corpo con scelte equilibrate e senza colpa.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_pms_o_gravidanza", ARTICLE, CYCLE, "Concepimento", phases = listOf("Luteale"),
            title = "Sintomi premestruali o gravidanza?",
            subtitle = "Perché si somigliano e come orientarsi.",
            durationMinutes = 3,
            bodyMarkdown = """
# PMS o inizio gravidanza?

Molti sintomi della **fase luteale** (premestruale) somigliano a quelli dei primi giorni di **gravidanza**, perché in entrambi i casi domina il **progesterone**. Ecco perché è difficile distinguerli solo dalle sensazioni.

## Sintomi che si sovrappongono
- **Seno teso** e sensibile.
- **Stanchezza** e sonnolenza.
- **Sbalzi d'umore**, gonfiore.
- Crampetti al basso ventre.

## Qualche differenza (non sempre netta)
- Nella gravidanza la stanchezza e la tensione al seno possono essere **più marcate e persistenti**.
- Possibili **nausea** e maggiore sensibilità agli odori, più tipiche dell'inizio gravidanza.
- Una **piccola perdita** da impianto invece della mestruazione attesa.

## L'unico modo per saperlo
I sintomi **non bastano**. Se la mestruazione tarda, l'unico modo affidabile è il **test di gravidanza** (dal giorno del ritardo). Nel frattempo, se c'è la possibilità di una gravidanza, comportati di conseguenza (per esempio con alcol e farmaci) finché non hai chiarezza.
            """.trimIndent()
        ),
        AcademyContent(
            "cyc_libido", ARTICLE, CYCLE, "Fondamenti",
            title = "Desiderio e libido durante il ciclo",
            subtitle = "Come cambia il desiderio nelle diverse fasi.",
            durationMinutes = 3,
            bodyMarkdown = """
# La libido segue il ciclo

Il desiderio sessuale non è costante: oscilla con gli ormoni lungo il mese, in modo diverso da persona a persona.

## L'andamento tipico
- **Ovulazione**: con il picco di estrogeni molte persone notano un **aumento del desiderio** — ha un senso biologico, è la fase fertile.
- **Fase luteale/premestruale**: la libido può **calare**, complici stanchezza, gonfiore e sbalzi d'umore.
- **Mestruazione**: varia molto: c'è chi ha desiderio (a volte i rapporti alleviano i crampi) e chi preferisce il riposo.

## Cosa influisce oltre agli ormoni
Stress, sonno, relazione, salute generale e alcuni **contraccettivi ormonali** possono modificare la libido.

## Quando parlarne
Un calo del desiderio **non** è un problema se non lo vivi come tale. Se invece ti pesa, è persistente o si associa a dolore durante i rapporti o altri sintomi, parlane con il medico: spesso si può fare qualcosa.
            """.trimIndent()
        ),

        // ----------------------------- Video ciclo (ID YouTube verificati) -----------------------------
        AcademyContent("cyc_v_funziona", VIDEO, CYCLE, "Fondamenti",
            title = "Come funziona il ciclo mestruale", subtitle = "Ovulazione e finestra fertile spiegate.",
            durationMinutes = 7, videoId = "3Gld7fYNxnc"),
        AcademyContent("cyc_v_muco", VIDEO, CYCLE, "Fertilità", phases = listOf("Ovulatoria"),
            title = "Ciclo, ovulazione e muco cervicale", subtitle = "Riconoscere i segnali di fertilità.",
            durationMinutes = 12, videoId = "ahSLn1Lr9L4"),
        AcademyContent("cyc_v_temp", VIDEO, CYCLE, "Fondamenti",
            title = "Temperatura basale: cos'è e come si prende", subtitle = "Guida pratica passo passo.",
            durationMinutes = 6, videoId = "HDMQJNxebXI"),
        AcademyContent("cyc_v_alim", VIDEO, CYCLE, "Alimentazione",
            title = "Alimentazione e ciclo: i consigli del nutrizionista", subtitle = "Variare la dieta per fase.",
            durationMinutes = 9, videoId = "j-dDzTyN0IE"),
        AcademyContent("cyc_v_concep", VIDEO, CYCLE, "Concepimento", phases = listOf("Ovulatoria"),
            title = "Concepimento e temperatura basale", subtitle = "Individuare i giorni giusti.",
            durationMinutes = 8, videoId = "jS6tc03MuX0"),
        AcademyContent("cyc_v_pcos", VIDEO, CYCLE, "Disturbi",
            title = "Ovaio policistico: sintomi, conseguenze e cura", subtitle = "Spiegazione dell'esperto.",
            durationMinutes = 11, videoId = "_ve3TKYHCHw"),
        AcademyContent("cyc_v_pcos2", VIDEO, CYCLE, "Disturbi",
            title = "PCOS: cos'è, sintomi e come si cura", subtitle = "Approfondimento medico.",
            durationMinutes = 9, videoId = "xdP9Oo7iTQM"),
        AcademyContent("cyc_v_yoga_pms", VIDEO, CYCLE, "Benessere", phases = listOf("Luteale"),
            title = "Yoga per la sindrome premestruale", subtitle = "Sequenza dolce per la fase luteale.",
            durationMinutes = 20, videoId = "HGD69q9GBZE"),
        AcademyContent("cyc_v_yoga_dolori", VIDEO, CYCLE, "Benessere", phases = listOf("Mestruale"),
            title = "Yoga contro i dolori mestruali", subtitle = "Movimento dolce per il sollievo.",
            durationMinutes = 15, videoId = "GQyH_FtAg7c"),
        AcademyContent("cyc_v_yoga_femm", VIDEO, CYCLE, "Benessere", phases = listOf("Mestruale", "Luteale"),
            title = "Yoga al femminile: ciclo e PMS", subtitle = "Pratica completa di 35 minuti.",
            durationMinutes = 35, videoId = "uo1AvFDmXDg"),
        AcademyContent("cyc_v_endo", VIDEO, CYCLE, "Disturbi",
            title = "L'endometriosi spiegata dal ginecologo", subtitle = "Cause, sintomi e cure.",
            durationMinutes = 10, videoId = "_twwlKlNhR4"),
        AcademyContent("cyc_v_contr_guida", VIDEO, CYCLE, "Contraccezione",
            title = "Spirale, preservativo o pillola? Guida alla contraccezione", subtitle = "Panoramica dei metodi.",
            durationMinutes = 13, videoId = "QJR36yX0-48"),
        AcademyContent("cyc_v_contr_orm", VIDEO, CYCLE, "Contraccezione",
            title = "Pillola, cerotto e anello: pro e contro", subtitle = "I contraccettivi ormonali spiegati dall'esperto.",
            durationMinutes = 9, videoId = "_iW2fBNo-VE"),
        AcademyContent("cyc_v_spirale", VIDEO, CYCLE, "Contraccezione",
            title = "La spirale anticoncezionale: come funziona", subtitle = "Spiegazione del professore.",
            durationMinutes = 8, videoId = "7TttIrxTG1M")
    )

    /* ============================ GRAVIDANZA ============================ */
    private val pregnancy = listOf(

        // ----------------------------- Trimestri -----------------------------
        AcademyContent(
            "pg_primo", ARTICLE, PREGNANCY, "Trimestri",
            title = "Primo trimestre: cosa aspettarsi (settimane 1–13)",
            subtitle = "Sintomi, sviluppo del bambino e priorità delle prime settimane.",
            durationMinutes = 6,
            bodyMarkdown = """
# Primo trimestre (settimane 1–13)

Il primo trimestre è quello dei **grandi cambiamenti invisibili**: il corpo si trasforma profondamente anche se la pancia non si vede ancora, ed è il periodo in cui si formano gli organi del bambino.

## Cosa succede nel tuo corpo
- **Nausea** e avversione a certi odori o cibi (la classica "nausea mattutina", che può presentarsi a qualsiasi ora).
- **Stanchezza** intensa: il corpo lavora moltissimo.
- **Seno** teso e sensibile.
- Sbalzi d'umore, legati anche al brusco aumento degli ormoni.
- Minzione più frequente.

Sono sintomi **comuni e in genere normali**, anche se l'intensità varia molto da persona a persona.

## Cosa succede al bambino
In poche settimane l'embrione passa da poche cellule a un piccolo organismo con **cuore che batte**, abbozzo di cervello e midollo (**tubo neurale**), arti e organi principali in formazione. È la fase in cui l'**acido folico** è più importante.

## Le priorità di questo trimestre
1. **Acido folico** e, se indicati, altri integratori.
2. **Prima visita** e avvio del percorso di assistenza.
3. **Idratazione e riposo**; mangiare poco e spesso aiuta con la nausea.
4. **Stop ad alcol e fumo**; rivedere con il medico farmaci e abitudini.
5. Attenzione ai **cibi a rischio** (vedi l'articolo sull'alimentazione).

## Quando contattare subito chi ti segue
- **Sanguinamento** o forti dolori addominali.
- Nausea e vomito così intensi da **non trattenere i liquidi**.
- Febbre alta.

Il primo trimestre può essere faticoso: concediti gentilezza e riposo. Spesso, dal secondo, molti disturbi migliorano.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_secondo", ARTICLE, PREGNANCY, "Trimestri",
            title = "Secondo trimestre: il periodo d'oro (settimane 14–27)",
            subtitle = "Più energia, i primi movimenti e l'ecografia morfologica.",
            durationMinutes = 5,
            bodyMarkdown = """
# Secondo trimestre (settimane 14–27)

Spesso è il trimestre **più sereno**: la nausea tende ad attenuarsi, torna l'**energia** e il pancione inizia a vedersi senza ancora pesare troppo.

## Cosa cambia nel corpo
- **Più energia** e appetito.
- **Pancia in crescita** visibile.
- Possibili **bruciori di stomaco**, stitichezza, congestione nasale.
- Pelle e capelli a volte più belli; possibili macchie cutanee.

## I primi movimenti
Tra la **16ª e la 24ª settimana** (più tardi alla prima gravidanza) molte persone iniziano a percepire i **primi movimenti** del bambino: all'inizio come piccole bolle o farfalle. È un momento emozionante e un primo "filo diretto".

## L'ecografia morfologica
Intorno alla **20ª settimana** si esegue l'**ecografia morfologica**, un controllo importante che valuta l'anatomia del bambino e la crescita. È spesso anche l'occasione, se lo desiderate, di scoprire il sesso.

## Su cosa concentrarsi
- **Postura e schiena**: il baricentro cambia; movimento dolce e attenzione ai pesi.
- **Idratazione** e fibre contro la stitichezza.
- Consolidare **routine di sonno, pasti e attività**.
- Eventuali corsi di **accompagnamento alla nascita**.

## Da non trascurare
Pur essendo un periodo più tranquillo, segnala sempre **sanguinamenti, dolore intenso, contrazioni regolari precoci** o disturbi visivi e mal di testa forti.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_terzo", ARTICLE, PREGNANCY, "Trimestri",
            title = "Terzo trimestre: verso la nascita (settimane 28–40)",
            subtitle = "Comfort, monitoraggio dei movimenti e preparazione al parto.",
            durationMinutes = 5,
            bodyMarkdown = """
# Terzo trimestre (settimane 28–40)

Nell'ultimo trimestre il bambino **cresce e prende peso** rapidamente e il corpo si prepara al parto. Il volume addominale può rendere meno comodi sonno, respiro e movimenti.

## Cosa puoi sentire
- **Stanchezza** di ritorno e sonno più disturbato.
- **Fiato corto**, bruciore di stomaco, gonfiore a gambe e caviglie.
- **Contrazioni di Braxton Hicks** (di "prova"): irregolari, non dolorose, che preparano l'utero.
- Maggiore pressione sul bacino e sulla vescica.

## I movimenti fetali come riferimento
In questa fase i **movimenti del bambino** sono un importante segnale di benessere. Impara il suo **ritmo abituale**: in caso di **riduzione netta** dei movimenti, contatta **subito** chi ti segue. (Vedi l'articolo dedicato.)

## Preparazione pratica
- **Borsa per l'ospedale** e documenti pronti per tempo.
- Piano per **raggiungere il punto nascita**.
- Decisioni su allattamento, primi giorni e supporto a casa.
- Controlli più ravvicinati.

## Riconoscere l'inizio del travaglio
Possibili segnali: **contrazioni regolari** che si fanno più intense e ravvicinate, **rottura delle acque**, perdita del tappo mucoso. (Vedi "Riconoscere il travaglio".)

## Segnali da non ignorare
Contatta subito chi ti segue per: **forte mal di testa o disturbi visivi**, **gonfiore improvviso** a viso e mani, dolore intenso, **sanguinamento**, **perdita di liquido**, febbre o **riduzione dei movimenti**. Meglio una chiamata in più.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_settimane", ARTICLE, PREGNANCY, "Trimestri",
            title = "Lo sviluppo settimana per settimana",
            subtitle = "Una mappa d'insieme della crescita del bambino.",
            durationMinutes = 5,
            bodyMarkdown = """
# Come cresce il bambino, settimana dopo settimana

Ogni settimana porta un piccolo passo avanti. Ecco una **mappa d'insieme** per orientarti (le dimensioni sono indicative).

## Settimane 4–8: le fondamenta
Dall'impianto si forma l'**embrione**. Compaiono il **tubo neurale**, l'abbozzo del **cuore** che inizia a battere e i primi abbozzi di arti. Alla 8ª settimana è grande come un **lampone**.

## Settimane 9–13: tutto al suo posto
L'embrione diventa **feto**: organi principali formati, dita di mani e piedi, primi movimenti (ancora impercettibili). Verso la 13ª è grande come una **prugna/limone**.

## Settimane 14–20: crescita e percezione
Il feto cresce in fretta, sviluppa l'**udito**, fa movimenti più coordinati. Verso la **20ª** è grande come una **banana** e arriva l'ecografia morfologica.

## Settimane 21–27: verso la vitalità
Si sviluppano **polmoni** (ancora immaturi) e ritmi di sonno-veglia; il bambino reagisce a suoni e luce. Pelle ancora sottile, peso in aumento.

## Settimane 28–36: peso e maturazione
Accumula **grasso**, i polmoni maturano, il cervello si sviluppa intensamente. Spesso si gira a **testa in giù**.

## Settimane 37–40: a termine
Dalla **37ª** la gravidanza è considerata **a termine**: il bambino è pronto a nascere. A 40 settimane è grande in media come una **piccola anguria**.

> Le settimane e le dimensioni sono **stime medie**: ogni bambino cresce con il proprio ritmo. La sezione gravidanza dell'app mostra la dimensione corrispondente alla tua settimana.
            """.trimIndent()
        ),

        // ----------------------------- Alimentazione -----------------------------
        AcademyContent(
            "pg_alimentazione", ARTICLE, PREGNANCY, "Alimentazione",
            title = "Alimentazione in gravidanza",
            subtitle = "Mangiare bene, non 'per due': qualità prima di quantità.",
            durationMinutes = 6,
            bodyMarkdown = """
# Alimentazione in gravidanza

Non serve **mangiare per due**: nel primo trimestre il fabbisogno calorico cambia poco, e aumenta solo moderatamente nei trimestri successivi. Conta soprattutto la **qualità** del cibo.

## Cosa privilegiare
- **Proteine** di qualità: legumi, uova ben cotte, pesce a basso contenuto di mercurio, carni magre.
- **Cereali integrali** e carboidrati complessi per energia stabile.
- **Frutta e verdura** lavate accuratamente: vitamine, minerali e fibre.
- **Calcio** (latticini pastorizzati, alternative fortificate) e **ferro** (legumi, verdure verdi, con vitamina C per l'assorbimento).
- **Acidi grassi omega-3** (pesce azzurro a basso mercurio): utili allo sviluppo.
- **Acqua** in abbondanza.

## Integratori
- **Acido folico** nel primo trimestre (spesso già da prima del concepimento).
- Altri (ferro, vitamina D, iodio…) **solo su indicazione** del medico.

## Cibi e abitudini da evitare
- **Alcol**: nessuna quantità è considerata sicura.
- **Carni e pesce crudi o poco cotti**, salumi non trattati dal calore, **uova crude**.
- **Latticini e formaggi non pastorizzati**; verdure non ben lavate.
- **Pesci ad alto mercurio** (alcuni pesci grandi predatori).
- **Caffeina** in eccesso: meglio limitarla.

Questi accorgimenti riducono il rischio di infezioni come **toxoplasmosi** e **listeriosi** (vedi articolo dedicato).

## Disturbi comuni e dieta
- **Nausea**: pasti piccoli e frequenti, cibi secchi al risveglio.
- **Reflusso** (terzo trimestre): porzioni ridotte, non sdraiarsi subito dopo i pasti.
- **Stitichezza**: fibre, acqua e movimento.

La regola d'oro resta semplice: **vario, regolare, sicuro**. Per dubbi specifici (diete particolari, intolleranze, valori alterati) confrontati con chi ti segue.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_nausea", ARTICLE, PREGNANCY, "Alimentazione", phases = listOf("Primo trimestre"),
            title = "Nausea in gravidanza: cosa aiuta",
            subtitle = "Strategie pratiche per i primi mesi e quando preoccuparsi.",
            durationMinutes = 4,
            bodyMarkdown = """
# Gestire la nausea in gravidanza

La **nausea**, con o senza vomito, è uno dei disturbi più comuni del **primo trimestre**. Nonostante il nome "nausea mattutina", può comparire **a qualsiasi ora**. Di solito migliora dopo le prime settimane.

## Strategie che spesso aiutano
- **Cracker o pane secco** al risveglio, prima di alzarti.
- **Pasti piccoli e frequenti**: lo stomaco vuoto peggiora la nausea.
- **Sapori delicati**, cibi freddi o a temperatura ambiente (odorano meno).
- **Zenzero** (tisane, biscotti) — molti lo trovano utile.
- **Idratazione a piccoli sorsi**, lontano dai pasti.
- **Evitare odori forti** e fritti.
- Riposo: la stanchezza accentua la nausea.

## Quando contattare il medico
La nausea diventa un problema se è **severa e persistente** al punto da:
- non riuscire a **trattenere liquidi o cibo**;
- perdere **peso**;
- avere segni di **disidratazione** (urine molto scure, vertigini, debolezza marcata).

Questa forma intensa (**iperemesi gravidica**) va valutata e trattata: **non** è solo "nausea forte" e non va affrontata da sole. In questi casi contatta chi ti segue.

## Una rassicurazione
La nausea, per quanto fastidiosa, in genere **non danneggia** il bambino e tende a risolversi. Annota intensità e fattori scatenanti: aiuta a gestirla meglio.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_cibi_rischio", ARTICLE, PREGNANCY, "Alimentazione",
            title = "Cibi da evitare: toxoplasmosi e listeriosi",
            subtitle = "Le regole di sicurezza alimentare per proteggere il bambino.",
            durationMinutes = 4,
            bodyMarkdown = """
# Sicurezza alimentare in gravidanza

Alcune infezioni alimentari, innocue di solito, possono essere pericolose in gravidanza. Due nomi da conoscere: **toxoplasmosi** e **listeriosi**. Con poche regole il rischio si riduce moltissimo.

## Toxoplasmosi
Si trasmette con carne cruda/poco cotta e con verdura o frutta contaminata da terra, oltre che dalle feci di gatto. Per prevenirla:
- **Cuoci bene** la carne; evita salumi crudi se non sei immune (chiedi al medico l'esame).
- **Lava accuratamente** frutta e verdura.
- Usa **guanti** per giardinaggio e per la lettiera del gatto (o falla pulire ad altri), e lava bene le mani.

## Listeriosi
Causata dal batterio *Listeria*, che resiste anche al freddo. Per prevenirla evita:
- **Latte e formaggi non pastorizzati**, formaggi molli a crosta fiorita o erborinati.
- **Pesce affumicato** e prodotti pronti refrigerati consumati senza ricottura.
- **Paté** e carni/salumi non trattati dal calore.

## Regole generali sempre valide
- **Cuoci bene** carne, pesce e uova.
- **Lava** mani, taglieri e utensili dopo il contatto con cibi crudi.
- Rispetta la **catena del freddo** e le scadenze.
- Lava sempre **frutta e verdura**.

## In sintesi
Niente crudo o non pastorizzato, tutto ben lavato e ben cotto. Sono accorgimenti semplici che proteggono te e il bambino: per i dubbi sui singoli alimenti, chiedi a chi ti segue.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_peso", ARTICLE, PREGNANCY, "Alimentazione",
            title = "Aumento di peso in gravidanza",
            subtitle = "Quanto è fisiologico e perché varia da persona a persona.",
            durationMinutes = 4,
            bodyMarkdown = """
# L'aumento di peso in gravidanza

Aumentare di peso in gravidanza è **fisiologico e necessario**: serve a sostenere la crescita del bambino, la placenta, il liquido amniotico, l'aumento del sangue e le riserve per l'allattamento.

## Quanto è "giusto"
Non esiste un numero unico: l'aumento consigliato **dipende dal peso di partenza** (dal BMI pre-gravidanza) ed è una valutazione **personalizzata** che fa il medico. In generale, chi parte da un peso più basso può aumentare di più, chi parte più in alto un po' meno.

## Come si distribuisce
L'aumento è **graduale**: minimo nel primo trimestre, più costante nel secondo e terzo. Non è solo "grasso": gran parte è bambino, placenta, liquidi e tessuti materni.

## Consigli pratici
- Punta sulla **qualità** del cibo, non sul "mangiare per due".
- **Movimento regolare**, se la gravidanza è fisiologica e chi ti segue è d'accordo.
- Evita sia le **restrizioni** drastiche sia gli eccessi.
- **Pesati** come indicato dal tuo curante, senza ossessione per la bilancia.

## Quando parlarne
Un aumento **troppo rapido** o **troppo scarso**, o un gonfiore improvviso (che è diverso dall'aumento di grasso), vanno segnalati. Il gonfiore rapido a viso e mani, in particolare, può essere un segnale da non ignorare. L'app permette di registrare il peso e seguirne l'andamento nel tempo, un dato utile alle visite.
            """.trimIndent()
        ),

        // ----------------------------- Benessere -----------------------------
        AcademyContent(
            "pg_movimento", ARTICLE, PREGNANCY, "Benessere",
            title = "Movimento e attività fisica in gravidanza",
            subtitle = "Quali attività fanno bene e come ascoltare il corpo.",
            durationMinutes = 5,
            bodyMarkdown = """
# Muoversi in gravidanza

Se la gravidanza è **fisiologica** e chi ti segue è d'accordo, un'attività fisica **moderata e regolare** fa bene: migliora umore, sonno, circolazione, mal di schiena, glicemia e prepara il corpo al parto.

## Attività indicate
- **Camminata**: semplice, sicura, adattabile.
- **Nuoto** e ginnastica in acqua: scaricano il peso, ottimi per schiena e gambe.
- **Yoga prenatale** e **pilates** adattati: postura, respirazione, pavimento pelvico.
- **Cyclette** e attività dolci a basso impatto.

## Come dosare lo sforzo
- Mantieni un'intensità che ti permetta di **parlare** mentre ti muovi.
- **Idratati** e non esagerare con il caldo.
- Privilegia la **regolarità** (poco e spesso) rispetto agli sforzi intensi e saltuari.
- Cura **postura e respirazione**; evita esercizi che comprimono l'addome o, dal secondo trimestre, lunghi periodi sdraiata sulla schiena.

## Attività da evitare
- Sport di **contatto** o con rischio di caduta (sci, equitazione, arti marziali).
- **Immersioni** subacquee.
- Sollevamento di **pesi importanti** e sforzi bruschi.

## Quando fermarsi e chiamare
Interrompi e contatta chi ti segue in caso di: **sanguinamento**, **contrazioni** regolari, perdita di liquido, dolore, capogiri, fiato corto inusuale o **riduzione dei movimenti** del bambino.

## In sintesi
Il movimento non è un dovere da prestazione: è **ascolto del corpo**. Pause e sonno valgono quanto l'esercizio. In caso di gravidanza a rischio, segui sempre le indicazioni specifiche del tuo medico.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_sonno", ARTICLE, PREGNANCY, "Benessere", phases = listOf("Terzo trimestre"),
            title = "Sonno e comfort con il pancione",
            subtitle = "Dormire meglio quando il riposo si fa difficile.",
            durationMinutes = 4,
            bodyMarkdown = """
# Dormire bene in gravidanza

Con il passare delle settimane, soprattutto nel **terzo trimestre**, dormire diventa più complicato: ingombro addominale, reflusso, risvegli per urinare, gambe pesanti e pensieri.

## La posizione
Dal secondo trimestre è consigliato dormire **su un fianco**, preferibilmente il **sinistro**: favorisce la circolazione verso il bambino. Aiutati con i **cuscini**:
- uno **tra le ginocchia** per la schiena;
- uno **sotto la pancia** per sostenerla;
- uno dietro la schiena per non rigirarti supina.

Esistono comodi **cuscini da gravidanza** a forma di C o U.

## Accorgimenti utili
- **Cena leggera e presto**; contro il reflusso, busto leggermente sollevato.
- **Riduci i liquidi** nelle ore serali (ma bevi bene di giorno).
- **Camera fresca e buia**, routine rilassante prima di dormire.
- Per le **gambe**: movimento di giorno, gambe sollevate la sera, evitare di restare ferma a lungo.
- Tecniche di **respirazione** o rilassamento per i pensieri notturni.

## Riposo anche di giorno
Se le notti sono frammentate, **brevi riposi diurni** aiutano a recuperare. Non sentirti in colpa: il corpo sta lavorando moltissimo.

## Quando parlarne
Segnala disturbi del sonno importanti, **russamento marcato** di nuova comparsa, gambe senza riposo molto fastidiose o ansia notturna persistente: ci sono accorgimenti e supporti specifici.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_disturbi", ARTICLE, PREGNANCY, "Benessere",
            title = "Mal di schiena e disturbi comuni",
            subtitle = "Reflusso, stitichezza, gambe gonfie: come alleviarli.",
            durationMinutes = 4,
            bodyMarkdown = """
# I piccoli disturbi della gravidanza

Molti fastidi della gravidanza sono **comuni e benigni**, legati ai cambiamenti del corpo. Ecco come alleviarli.

## Mal di schiena
Il baricentro si sposta e i legamenti si ammorbidiscono. Aiutano:
- **Postura** curata, evitare di inarcare la schiena;
- **movimento dolce** e rinforzo dolce di schiena e pavimento pelvico;
- scarpe comode, evitare pesi e tacchi alti;
- **calore** locale e, se serve, fisioterapia.

## Reflusso e bruciore di stomaco
- **Pasti piccoli e frequenti**, masticare con calma.
- Evitare cibi grassi, piccanti, caffè e pasti abbondanti la sera.
- **Non sdraiarsi** subito dopo aver mangiato; busto sollevato la notte.

## Stitichezza
- Più **fibre** (frutta, verdura, integrali), **acqua** e **movimento**.
- Non trattenere lo stimolo.

## Gambe gonfie e pesanti
- **Muoviti** spesso, evita di restare ferma a lungo.
- **Solleva le gambe** quando puoi; calze a compressione se consigliate.
- Limita il sale in eccesso.

## Emorroidi e crampi
Frequenti nella seconda metà: fibre, idratazione, movimento e, per i crampi notturni, stretching dolce del polpaccio.

## Quando non è "solo un disturbo"
Attenzione a: **gonfiore improvviso** di viso e mani, **mal di testa forte** o disturbi visivi, dolore intenso, **sanguinamento**. Questi segnali vanno riferiti **subito** a chi ti segue: vedi l'articolo "Segnali da non ignorare".
            """.trimIndent()
        ),
        AcademyContent(
            "pg_emozioni", ARTICLE, PREGNANCY, "Benessere",
            title = "Benessere emotivo in gravidanza",
            subtitle = "Emozioni, ansia e quando chiedere supporto.",
            durationMinutes = 4,
            bodyMarkdown = """
# Le emozioni della gravidanza

La gravidanza è un'**altalena emotiva**: gioia, attesa, ma anche ansia, paura e dubbi. Gli ormoni, i cambiamenti del corpo e della vita rendono tutto più intenso. È del tutto **normale** e parlarne fa bene.

## Cosa puoi sentire
- **Sbalzi d'umore** e maggiore sensibilità.
- **Ansie** sul parto, sulla salute del bambino, sul "essere all'altezza".
- Cambiamenti nel rapporto con il **corpo** e con il partner.

## Cosa aiuta
- **Parlarne**: con il partner, persone di fiducia, altre persone in attesa.
- **Riposo** e attività piacevoli; movimento dolce.
- Tecniche di **respirazione e rilassamento**.
- **Informarsi** senza sovraccaricarsi: scegli fonti affidabili e poche.
- Corsi di **accompagnamento alla nascita**: riducono molto la paura dell'ignoto.

## Quando chiedere supporto
Non è solo "fragilità del momento" se compaiono:
- **tristezza profonda** o perdita di interesse per tutto, persistenti;
- **ansia** che interferisce con la vita quotidiana;
- difficoltà a dormire **non** legate al pancione;
- pensieri che ti spaventano.

In questi casi **parlane con chi ti segue**: il disagio emotivo in gravidanza è frequente, riconosciuto e **si può affrontare** con il giusto supporto. Chiedere aiuto è un atto di cura verso te stessa e il bambino.

## Dopo la nascita
Anche il **post-partum** può portare un periodo di fragilità emotiva. Sapere in anticipo che è comune aiuta a riconoscerlo e a chiedere sostegno per tempo.
            """.trimIndent()
        ),

        // ----------------------------- Salute -----------------------------
        AcademyContent(
            "pg_movimenti", ARTICLE, PREGNANCY, "Salute", phases = listOf("Terzo trimestre"),
            title = "Movimenti fetali: impararli e contarli",
            subtitle = "Un riferimento importante di benessere nel terzo trimestre.",
            durationMinutes = 4,
            bodyMarkdown = """
# I movimenti del bambino

I **movimenti fetali** sono uno dei segnali più preziosi del benessere del bambino, soprattutto nel **terzo trimestre**. Imparare a conoscerli ti permette di accorgerti se qualcosa cambia.

## Quando si iniziano a sentire
- Tra la **16ª e la 24ª settimana** (più tardi alla prima gravidanza), come bollicine o farfalle.
- Col tempo diventano **più forti e riconoscibili**: calci, rotolamenti, singhiozzi.

## Conoscere il "ritmo" del tuo bambino
Ogni bambino ha un proprio **schema** di attività e riposo nell'arco della giornata. La cosa importante non è un numero fisso, ma **conoscere il suo solito ritmo**. Molti sono più attivi dopo i pasti o quando ti sdrai.

## Come "contarli" se hai un dubbio
Se ti sembra che si muova meno, fermati in un momento tranquillo (spesso dopo un pasto), **sdraiata su un fianco**, e concentrati sui movimenti. Una percezione del solito ritmo è rassicurante.

## Il segnale da non ignorare
> Se percepisci una **riduzione netta** o un cambiamento marcato dei movimenti, **non aspettare**: contatta **subito** chi ti segue o il punto nascita.

Non esiste "disturbare per niente": è sempre giusto far controllare. Meglio una verifica in più. Evita rimedi "fai da te" (come bevande zuccherate per "svegliarlo") al posto di un controllo: se hai un dubbio, chiama.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_visite", ARTICLE, PREGNANCY, "Salute",
            title = "Visite ed esami: il calendario dei controlli",
            subtitle = "Tenere tutto in ordine riduce l'ansia e protegge la salute.",
            durationMinutes = 5,
            bodyMarkdown = """
# Il percorso dei controlli in gravidanza

La gravidanza fisiologica prevede una serie di **visite ed esami** a scadenze regolari, per monitorare la salute tua e del bambino. Conoscerne il senso aiuta a viverli con serenità.

## Gli appuntamenti principali
- **Prima visita**: conferma, anamnesi, impostazione del percorso, prescrizione di acido folico ed esami iniziali.
- **Ecografie**: in genere una per trimestre, con tempi e finalità diverse (datazione, **morfologica intorno alla 20ª settimana**, crescita nel terzo trimestre).
- **Esami del sangue e delle urine**: a tappe, per anemia, infezioni, gruppo sanguigno e altro.
- **Curva da carico di glucosio (OGTT)**: di solito tra la **24ª e la 28ª settimana**, per il diabete gestazionale (anticipata se ci sono fattori di rischio).
- **Tampone vaginale-rettale** per lo *Streptococco* (verso fine gravidanza).
- **Controllo della pressione** e del peso a ogni visita.

## Come organizzarti
- Tieni un **archivio unico** (cartella o app) per **referti, date ed esami**.
- Prepara una **lista di domande** prima di ogni visita (sintomi, farmaci, vaccini, lavoro, dubbi sul parto).
- Annota i **valori** che ti vengono dati: ti aiuteranno a seguire l'andamento.

## Personalizzazione
Il calendario può cambiare in base alla tua storia clinica e a eventuali condizioni: segui sempre le indicazioni di chi ti segue. L'app ti aiuta a non perdere appuntamenti e a portare in visita un quadro ordinato.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_segnali", ARTICLE, PREGNANCY, "Salute",
            title = "Segnali da non ignorare",
            subtitle = "Quando contattare subito chi ti segue.",
            durationMinutes = 4,
            bodyMarkdown = """
# Segnali d'allarme in gravidanza

La maggior parte dei fastidi della gravidanza è benigna, ma alcuni sintomi richiedono attenzione **immediata**. Conoscerli non serve a spaventarsi, ma a sapere **quando agire**.

## Contatta SUBITO chi ti segue (o i servizi di emergenza) se compaiono:
- **Sanguinamento** vaginale (soprattutto se abbondante).
- **Perdita di liquido** dalla vagina (possibile rottura delle acque).
- **Dolore addominale o pelvico intenso** e persistente.
- **Contrazioni regolari** prima del termine.
- **Forte mal di testa**, soprattutto se con **disturbi visivi** (lampi, vista offuscata).
- **Gonfiore improvviso** di viso, mani o piedi.
- **Riduzione netta dei movimenti** del bambino (nel terzo trimestre).
- **Febbre alta**, brividi, bruciore importante a urinare.
- **Forte difficoltà a respirare**, dolore al petto, **convulsioni**, svenimento.
- Vomito incoercibile con impossibilità di trattenere liquidi.

## La regola d'oro
> In caso di dubbio, è sempre meglio **una chiamata in più**. Non temere di "disturbare": valutare un sintomo è esattamente il compito di chi ti assiste.

## Tienilo a portata di mano
Salva i **contatti** del tuo punto nascita e di chi ti segue, e tienili accessibili (anche al partner). In gravidanza avanzata, prepara per tempo il piano per raggiungere l'ospedale.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_diabete", ARTICLE, PREGNANCY, "Salute",
            title = "Diabete gestazionale: cosa sapere",
            subtitle = "Come si individua e perché si gestisce con serenità.",
            durationMinutes = 4,
            bodyMarkdown = """
# Diabete gestazionale

Il **diabete gestazionale** è un aumento della glicemia che compare **in gravidanza** in chi non era diabetica prima. È legato agli ormoni placentari che riducono l'azione dell'insulina. È **frequente** e, gestito bene, ha in genere esito favorevole.

## Come si individua
Di solito con la **curva da carico di glucosio (OGTT)** tra la **24ª e la 28ª settimana**, anticipata se ci sono fattori di rischio (familiarità, sovrappeso, età, precedente diabete gestazionale).

## Perché si controlla
Una glicemia troppo alta può favorire una **crescita eccessiva** del bambino e altre complicanze al parto. Tenerla nei valori giusti **riduce molto** questi rischi.

## Come si gestisce
Nella maggior parte dei casi è sufficiente:
- **Alimentazione equilibrata**, con attenzione a quantità e qualità dei carboidrati distribuiti nei pasti.
- **Movimento regolare** (se non controindicato).
- **Automonitoraggio** della glicemia, secondo le indicazioni.

In una parte dei casi serve anche una terapia (per esempio insulina), del tutto **compatibile** con la gravidanza, su prescrizione medica.

## Dopo il parto
Il diabete gestazionale di solito **si risolve** dopo la nascita, ma indica una maggiore predisposizione futura: utili controlli successivi e uno stile di vita sano.

## In pratica
Non è una "colpa" né una condanna: è una condizione comune e gestibile. Seguire le indicazioni del team che ti assiste è la chiave. Registrare alimentazione e valori aiuta a tenere tutto sotto controllo.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_pressione", ARTICLE, PREGNANCY, "Salute",
            title = "Pressione e preeclampsia",
            subtitle = "Perché si misura la pressione a ogni visita.",
            durationMinutes = 4,
            bodyMarkdown = """
# Pressione arteriosa e preeclampsia

Il controllo della **pressione** a ogni visita non è una formalità: serve a individuare per tempo i disturbi **ipertensivi** della gravidanza, tra cui la **preeclampsia**.

## Cos'è la preeclampsia
È una condizione che può comparire di solito **dopo la 20ª settimana**, caratterizzata da **pressione alta** associata a segni di sofferenza di alcuni organi (per esempio presenza di proteine nelle urine). Va riconosciuta e gestita perché può avere conseguenze per la madre e il bambino.

## Segnali a cui prestare attenzione
Contatta **subito** chi ti segue in caso di:
- **Mal di testa forte** e persistente.
- **Disturbi visivi**: lampi di luce, vista offuscata.
- **Gonfiore improvviso** di viso, mani e piedi.
- **Dolore intenso** nella parte alta della pancia, sotto le costole.
- Aumento di peso molto rapido.

## Chi è più a rischio
Prima gravidanza, ipertensione preesistente, alcune condizioni mediche, gravidanze gemellari, familiarità. In presenza di fattori di rischio, chi ti segue può indicare misure preventive.

## Cosa fare
- **Non saltare** i controlli: la misurazione regolare della pressione è lo strumento principale.
- Se ti viene chiesto, **misura la pressione a casa** e annota i valori.
- Riferisci tempestivamente i **segnali** sopra elencati.

La preeclampsia si gestisce tanto meglio quanto prima viene individuata: ecco perché i controlli regolari sono così importanti.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_travaglio_riconoscere", ARTICLE, PREGNANCY, "Salute", phases = listOf("Terzo trimestre"),
            title = "Riconoscere l'inizio del travaglio",
            subtitle = "Veri segnali del travaglio e quando andare in ospedale.",
            durationMinutes = 4,
            bodyMarkdown = """
# Sta iniziando il travaglio?

Verso fine gravidanza è normale chiedersi "è il momento?". Imparare a distinguere i **segnali veri** del travaglio dai "falsi allarmi" aiuta a vivere l'attesa con più calma.

## I segnali del travaglio
- **Contrazioni regolari** che diventano progressivamente **più intense, lunghe e ravvicinate** e **non passano** con il riposo o cambiando posizione.
- **Rottura delle acque**: perdita di liquido, a fiotto o a piccole perdite continue.
- **Perdita del tappo mucoso**: muco denso, a volte striato di sangue (può precedere il travaglio anche di giorni).
- Dolore tipo crampo che dalla schiena si irradia in avanti.

## Le contrazioni "di prova" (Braxton Hicks)
Sono **irregolari**, non aumentano di intensità, spesso passano con riposo, idratazione o un cambio di posizione. Sono normali e preparano l'utero, ma **non** sono travaglio.

## Quando contattare il punto nascita
- Quando le **contrazioni** diventano regolari e ravvicinate (segui le indicazioni che ti hanno dato, spesso una certa frequenza per un certo tempo).
- **Subito** se si **rompono le acque** (annota ora e colore del liquido), se il liquido è **verdastro**, se c'è **sanguinamento** o se percepisci **meno movimenti** del bambino.

## Prepararsi
Tieni pronti **borsa e documenti** e il piano per raggiungere l'ospedale. Avere chiari i segnali e i numeri da chiamare riduce molto l'ansia del momento.
            """.trimIndent()
        ),

        // ----------------------------- Parto -----------------------------
        AcademyContent(
            "pg_parto", ARTICLE, PREGNANCY, "Parto", phases = listOf("Terzo trimestre"),
            title = "Prepararsi al parto",
            subtitle = "Organizzazione pratica e mentale delle ultime settimane.",
            durationMinutes = 5,
            bodyMarkdown = """
# Verso il parto: prepararsi con serenità

Arrivare preparate al parto **riduce l'ansia** e ti aiuta a vivere il momento con più consapevolezza. La preparazione è insieme **pratica** e **mentale**.

## La borsa per l'ospedale
Preparala intorno alla **35ª–36ª settimana**. In genere servono:
- **Documenti**: documento d'identità, tessera sanitaria, cartella della gravidanza ed esami.
- Per te: capi comodi, biancheria, assorbenti post-parto, articoli per l'igiene, ciabatte.
- Per il bambino: body, tutine, qualcosa per coprirlo, pannolini (se richiesti).
- Eventuale occorrente per l'allattamento.

## Il piano pratico
- Come **raggiungere il punto nascita** (e un piano B).
- **Numeri utili** salvati e condivisi con il partner.
- Organizzazione a casa per i **primi giorni** dopo il rientro.

## Prepararsi mentalmente
- Informati su **fasi del travaglio**, **respirazione** e **posizioni** (vedi gli articoli dedicati).
- Considera un **corso di accompagnamento alla nascita**.
- Rifletti su ciò che è importante per te (gestione del dolore, chi ti accompagna): un **piano del parto** flessibile aiuta a comunicare i tuoi desideri, restando aperti a ciò che servirà davvero.

## Mantenere la flessibilità
Ogni parto è diverso e può non andare "secondo programma": va benissimo. L'obiettivo è arrivare **informate e sostenute**, non avere il controllo su tutto. Fidati del tuo corpo e del team che ti assiste.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_fasi_travaglio", ARTICLE, PREGNANCY, "Parto", phases = listOf("Terzo trimestre"),
            title = "Le fasi del travaglio e del parto",
            subtitle = "Cosa succede, dalla dilatazione alla nascita.",
            durationMinutes = 5,
            bodyMarkdown = """
# Le fasi del travaglio

Sapere **cosa aspettarsi** rende il travaglio meno spaventoso. Il parto si articola in tre fasi principali (più una "fase prodromica" iniziale).

## Fase prodromica (pre-travaglio)
Contrazioni ancora **irregolari**, collo dell'utero che inizia ad ammorbidirsi. Può durare ore o giorni. È il momento di **riposare, idratarsi e mangiare leggero**, conservando le energie.

## 1. Dilatazione
È la fase più lunga. Le contrazioni diventano **regolari, intense e ravvicinate** e il collo dell'utero si **dilata** progressivamente (fino a circa 10 cm). Si distingue una fase iniziale più lenta e una **fase attiva** più rapida e intensa. Qui aiutano **respirazione, movimento, posizioni** e le tecniche apprese; è anche la fase in cui si valutano eventuali metodi di **gestione del dolore**.

## 2. Espulsione (nascita)
A dilatazione completa arriva la **spinta**: con le contrazioni e la collaborazione attiva, il bambino percorre il canale del parto fino a **nascere**. Le posizioni e la guida dell'ostetrica sono preziose.

## 3. Secondamento
Dopo la nascita, con alcune contrazioni, viene espulsa la **placenta**. È una fase breve ma importante, durante la quale di solito puoi già accogliere il bambino **pelle a pelle**.

## Ogni travaglio è unico
Durata e intensità variano moltissimo da persona a persona e da una gravidanza all'altra. Conoscere le fasi ti aiuta a **orientarti** e a collaborare con chi ti assiste, qualunque sia il percorso del tuo parto.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_dolore_parto", ARTICLE, PREGNANCY, "Parto", phases = listOf("Terzo trimestre"),
            title = "Respirazione e gestione del dolore nel parto",
            subtitle = "Strumenti naturali e opzioni mediche, senza giudizio.",
            durationMinutes = 4,
            bodyMarkdown = """
# Gestire il dolore del travaglio

Non esiste un solo modo "giusto" di affrontare il dolore del parto: esistono **tante opzioni** e la scelta è personale. Conoscerle ti permette di decidere con consapevolezza.

## Metodi non farmacologici
- **Respirazione**: respiri lenti e profondi durante le contrazioni, espirazione lunga; aiutano a gestire l'onda del dolore e a ossigenare bene.
- **Movimento e posizioni**: camminare, dondolare il bacino, posizioni verticali o a quattro zampe possono alleviare e favorire la discesa del bambino.
- **Acqua calda** (doccia o vasca, dove disponibile).
- **Massaggio** e contropressione sulla schiena.
- **Palla da parto**, calore locale, ambiente tranquillo e supporto continuo di chi ti accompagna.
- Tecniche di **rilassamento** e visualizzazione apprese nei corsi.

## Metodi farmacologici
- **Analgesia epidurale**: riduce in modo importante il dolore mantenendo la partecipazione; disponibilità e modalità dipendono dal punto nascita.
- Altri farmaci analgesici, secondo valutazione clinica.

## Come scegliere
- Informati **prima** sulle opzioni del tuo punto nascita.
- Tieni un atteggiamento **flessibile**: puoi iniziare con metodi naturali e cambiare idea durante il travaglio. Va bene così.
- **Nessuna scelta è "migliore" in assoluto**: la scelta giusta è quella che fa stare bene te, in sicurezza.

## Senza giudizio
Chiedere l'epidurale non è "fallire", così come farne a meno non è "più meritorio". L'obiettivo è un parto **sicuro** e un'esperienza vissuta nel modo più sereno possibile per te.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_cesareo", ARTICLE, PREGNANCY, "Parto",
            title = "Il parto cesareo",
            subtitle = "Quando si fa, come funziona e il recupero.",
            durationMinutes = 4,
            bodyMarkdown = """
# Il parto cesareo

Il **taglio cesareo** è un intervento chirurgico con cui il bambino nasce attraverso un'incisione dell'addome e dell'utero. Può essere **programmato** o **deciso durante il travaglio** se la situazione lo richiede.

## Quando può essere indicato
- Posizione del bambino non adatta al parto vaginale (per esempio podalica, in certi casi).
- **Placenta** che ostruisce il canale del parto.
- Sofferenza del bambino o mancata progressione del travaglio.
- Alcune condizioni materne specifiche.
- Precedenti cesarei, valutati caso per caso.

La decisione è sempre **clinica** e mira alla sicurezza di mamma e bambino.

## Come funziona
Si esegue di solito con **anestesia loco-regionale** (spinale o epidurale), così puoi essere **sveglia** e accogliere il bambino subito dopo la nascita. L'intervento dura in genere poco; spesso è possibile il contatto **pelle a pelle** precoce.

## Il recupero
- È un **intervento chirurgico**: il recupero richiede qualche giorno in più rispetto al parto vaginale.
- All'inizio possono servire **antidolorifici** (compatibili con l'allattamento) e attenzione alla **ferita**.
- Movimenti graduali, evitare sforzi e pesi nelle prime settimane.
- L'**allattamento** è possibile: trova posizioni comode che non premano sulla ferita.

## Vivere il cesareo con serenità
Un cesareo, programmato o no, **non è un parto "di serie B"**: è una nascita a tutti gli effetti. Informarti prima — anche se prevedi un parto vaginale — ti aiuta ad affrontarlo con più tranquillità se dovesse servire.
            """.trimIndent()
        ),

        // ----------------------------- Allattamento -----------------------------
        AcademyContent(
            "pg_allattamento", ARTICLE, PREGNANCY, "Allattamento",
            title = "Allattamento: prepararsi prima della nascita",
            subtitle = "Aspettative realistiche, attacco e sostegno.",
            durationMinutes = 5,
            bodyMarkdown = """
# Prepararsi all'allattamento

Informarsi **prima** della nascita rende l'allattamento meno improvvisato e aiuta a superare i primi giorni, spesso i più impegnativi.

## I primi giorni
- Nelle prime ore esce il **colostro**, un latte denso e prezioso, ricco di anticorpi: ne basta poco.
- La **montata lattea** arriva di solito dopo alcuni giorni.
- Il **contatto pelle a pelle** subito dopo la nascita favorisce l'avvio.

## L'attacco corretto
È la chiave per allattare senza dolore ed efficacemente. In generale:
- Il bambino prende **gran parte dell'areola**, non solo il capezzolo.
- Bocca **ben aperta**, labbra estroflesse, mento contro il seno.
- Se senti **dolore persistente** o vedi capezzoli danneggiati, l'attacco va corretto: **chiedi aiuto**.

## Allattamento a richiesta
Nei primi tempi si allatta **a richiesta**, senza orari rigidi: i ritmi del neonato sono frequenti e variabili. I segnali di fame precoci (si porta le mani alla bocca, cerca) precedono il pianto.

## Come capire se va bene
Indicatori rassicuranti: il bambino **bagna** e sporca regolarmente i pannolini, è reattivo e **cresce** ai controlli. Il peso si valuta nel tempo, non sul singolo giorno.

## Chiedere sostegno
L'allattamento è naturale ma **si impara**: difficoltà iniziali sono comuni. Ostetriche, consulenti dell'allattamento e gruppi di sostegno sono risorse preziose. Apri una nota con le **domande** da fare.

## Se l'allattamento al seno non è possibile o scelto
Va benissimo: l'importante è che il bambino sia **nutrito e amato**. Per il **latte artificiale**, segui le indicazioni del pediatra su tipo, preparazione e igiene. Nessun senso di colpa: ogni famiglia trova la propria strada.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_postpartum", ARTICLE, PREGNANCY, "Allattamento",
            title = "Post-partum: il recupero dopo la nascita",
            subtitle = "Corpo, emozioni e quando chiedere aiuto nelle prime settimane.",
            durationMinutes = 5,
            bodyMarkdown = """
# Il post-partum: prendersi cura di sé

Le settimane dopo il parto (il **puerperio**) sono un periodo di grandi trasformazioni: il corpo si ricostruisce, le emozioni sono intense e tutto è nuovo. Prendersi cura di **sé** è parte della cura del bambino.

## Il corpo che recupera
- **Perdite (lochiazioni)**: sanguinamento che si riduce gradualmente nelle settimane successive.
- L'**utero** torna lentamente alle dimensioni normali (a volte con contrazioni, specie durante le poppate).
- **Ferite** (del perineo o del cesareo) da curare con igiene e riposo.
- Stanchezza, sudorazioni, cambiamenti del seno.

Concediti **riposo** quando puoi e non avere fretta di "tornare come prima".

## Le emozioni
- Nei primi giorni è comune una fase di **maggiore sensibilità e pianto facile** (il cosiddetto "baby blues"), legata a ormoni e stanchezza, che tende a passare.
- Se però **tristezza, ansia o senso di vuoto** sono intensi, persistono oltre le prime settimane o ti impediscono di prenderti cura di te o del bambino, potrebbe trattarsi di **depressione post-partum**: è frequente, **non è una colpa** e **si cura**. Parlane con il medico.

## Quando chiedere aiuto medico
Contatta subito chi ti segue in caso di: **sanguinamento abbondante** improvviso, **febbre**, dolore o arrossamento di una ferita, dolore al seno con febbre (possibile mastite), forte mal di testa, oppure pensieri che ti spaventano.

## Chiedere e accettare sostegno
Il post-partum **non si affronta da sole**: appoggiati a partner, famiglia e operatori. Accettare aiuto pratico (pasti, riposo, gestione della casa) ti permette di concentrarti sul recupero e sul legame con il bambino.
            """.trimIndent()
        ),

        // ----------------------------- Approfondimenti aggiunti -----------------------------
        AcademyContent(
            "pg_caffeina", ARTICLE, PREGNANCY, "Alimentazione",
            title = "Caffè e caffeina in gravidanza",
            subtitle = "Quanta se ne può prendere e dove si nasconde.",
            durationMinutes = 3,
            bodyMarkdown = """
# Caffeina in gravidanza

La caffeina attraversa la placenta e il bambino la smaltisce più lentamente: per questo in gravidanza se ne raccomanda un consumo **moderato**.

## Quanto
Le indicazioni più diffuse suggeriscono di **non superare circa 200 mg al giorno** di caffeina, pari indicativamente a **1–2 tazzine di caffè**. Meglio confrontarsi con chi ti segue per il tuo caso.

## Dove si nasconde
Non solo nel caffè:
- **Tè** (anche verde) e **bevande tipo cola** o energy drink.
- **Cioccolato**, soprattutto fondente.
- Alcuni **farmaci** da banco (es. per il mal di testa): leggi le etichette.

## Consigli pratici
- Tieni il conto delle diverse fonti nell'arco della giornata.
- Valuta alternative: caffè d'orzo, tisane **sicure** in gravidanza (alcune erbe sono sconsigliate: chiedi al medico).
- Ricorda che la caffeina può peggiorare reflusso e insonnia, già comuni in gravidanza.

## In sintesi
Non serve eliminarla del tutto, ma **moderarla** e considerare tutte le fonti. Nel dubbio, riduci e chiedi a chi ti segue.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_viaggi", ARTICLE, PREGNANCY, "Benessere",
            title = "Viaggiare in gravidanza",
            subtitle = "Auto, treno e aereo: accorgimenti per spostarsi serene.",
            durationMinutes = 4,
            bodyMarkdown = """
# Viaggiare in gravidanza

Se la gravidanza è fisiologica, di solito si può viaggiare. Servono solo qualche accorgimento e un po' di organizzazione. Il periodo spesso più comodo è il **secondo trimestre**.

## In auto
- Indossa **sempre la cintura**: la parte bassa **sotto** il pancione (sulle anche), quella diagonale tra i seni e di lato alla pancia.
- Fai **soste frequenti** per camminare e sgranchirti: aiuta la circolazione.
- Evita viaggi lunghissimi senza pause.

## In treno
Comodo per muoversi e andare in bagno. Alzati ogni tanto.

## In aereo
- In genere consentito fino a un certo numero di settimane: **verifica le regole della compagnia** (alcune chiedono un certificato in gravidanza avanzata).
- **Muovi le gambe**, bevi acqua, valuta calze a compressione per i voli lunghi.
- Allaccia la cintura sotto il pancione.

## Prima di partire
- Porta con te la **documentazione** della gravidanza e i contatti utili.
- Informati sull'assistenza sanitaria a destinazione e su eventuali vaccinazioni o rischi (alcune mete sono sconsigliate).
- Evita spostamenti lontani vicino al termine.

In caso di gravidanza a rischio o sintomi, chiedi sempre a chi ti segue prima di partire.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_lavoro", ARTICLE, PREGNANCY, "Benessere",
            title = "Lavoro e gravidanza",
            subtitle = "Comfort, sicurezza e organizzazione durante la giornata.",
            durationMinutes = 3,
            bodyMarkdown = """
# Lavorare in gravidanza

Molte persone lavorano serenamente in gravidanza. Qualche accorgimento aiuta a stare meglio e a tutelare la salute.

## Comfort sul lavoro
- **Alzati e muoviti** regolarmente se stai seduta a lungo; se stai molto in piedi, cerca momenti per sederti e sollevare le gambe.
- Tieni a portata **acqua** e qualche snack per nausea e cali di energia.
- Cura la **postura** e, se serve, un supporto lombare.

## Sicurezza
Alcune mansioni (sostanze chimiche, sollevamento di pesi importanti, turni notturni pesanti, forte esposizione a vibrazioni o calore) possono richiedere **adattamenti**. Parlane con il medico e con il medico del lavoro: esistono tutele specifiche per la gravidanza.

## Organizzazione
- Gestisci gli appuntamenti per **visite ed esami**.
- Pianifica per tempo i passaggi pratici legati all'assenza.

## Ascolta il corpo
Stanchezza e nausea possono incidere: concediti pause e non sentirti in dovere di "fare come prima". Se compaiono sintomi che ti preoccupano, fermati e senti chi ti segue.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_sesso", ARTICLE, PREGNANCY, "Benessere",
            title = "Sesso e intimità in gravidanza",
            subtitle = "Cosa è normale e quando è meglio chiedere.",
            durationMinutes = 3,
            bodyMarkdown = """
# Intimità in gravidanza

Nella maggior parte delle gravidanze fisiologiche l'attività sessuale è **sicura** e non fa male al bambino, che è protetto dal sacco amniotico e dalla muscolatura uterina.

## Cosa può cambiare
- Il **desiderio** può aumentare o diminuire nei diversi trimestri: è normale.
- Con il pancione può servire **cambiare posizione** per stare comode.
- Dopo i rapporti, lievi contrazioni o piccole perdite possono capitare; in genere passano.

## Quando è meglio evitare o chiedere prima
In alcune situazioni il medico può consigliare cautela o astensione, ad esempio:
- **Sanguinamenti** o perdite di liquido.
- **Placenta previa** o altre condizioni specifiche.
- **Minaccia di parto pretermine** o rottura delle membrane.

In questi casi, o se hai dubbi, **chiedi a chi ti segue**.

## L'intimità è anche altro
Vicinanza, coccole e dialogo con il partner contano quanto il resto: la gravidanza cambia il corpo e le emozioni, e parlarne aiuta a viverla insieme.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_anemia", ARTICLE, PREGNANCY, "Salute",
            title = "Anemia in gravidanza",
            subtitle = "Perché il ferro è importante per te e per il bambino.",
            durationMinutes = 3,
            bodyMarkdown = """
# Anemia e ferro in gravidanza

In gravidanza il fabbisogno di **ferro** aumenta: serve a produrre più sangue e a sostenere la crescita del bambino e della placenta. Per questo l'**anemia da carenza di ferro** è frequente, soprattutto nella seconda metà.

## Segnali a cui fare attenzione
- **Stanchezza** marcata, debolezza.
- **Pallore**, fiato corto, palpitazioni.
- Capogiri, mal di testa, difficoltà di concentrazione.

## Come prevenirla
- Alimenti ricchi di **ferro**: legumi, verdure a foglia verde, cereali integrali; carne e pesce se li mangi.
- Abbina la **vitamina C** (agrumi, kiwi, peperoni) per assorbirlo meglio.
- **Tè e caffè** ai pasti riducono l'assorbimento: meglio lontano dai pasti.

## Controlli e integrazione
L'anemia si individua con i normali **esami del sangue** previsti in gravidanza. Se necessario, il medico prescrive un'**integrazione di ferro** (da non assumere di iniziativa). Segui dosi e modalità indicate: a volte il ferro dà disturbi intestinali, gestibili con qualche accorgimento.

Tenere il ferro a posto significa più energia per te e una crescita serena per il bambino.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_piano_parto", ARTICLE, PREGNANCY, "Parto", phases = listOf("Terzo trimestre"),
            title = "Il piano del parto",
            subtitle = "Mettere per iscritto desideri e preferenze, restando flessibili.",
            durationMinutes = 3,
            bodyMarkdown = """
# Il piano del parto

Il **piano del parto** è un documento in cui metti per iscritto le tue **preferenze** per il travaglio, la nascita e i primi momenti con il bambino. Serve a comunicarle al team che ti assiste — non è un contratto rigido.

## Cosa può contenere
- Chi vuoi accanto come **accompagnatore**.
- Preferenze per la **gestione del dolore** (metodi naturali, epidurale…).
- **Posizioni** e libertà di movimento in travaglio.
- Desideri per il **post-nascita**: contatto pelle a pelle, allattamento, taglio del cordone.
- Eventuali aspetti culturali o personali importanti.

## Come prepararlo
- Informati prima (corsi di accompagnamento alla nascita, colloqui con l'ostetrica).
- Visita il **punto nascita** e chiedi cosa è possibile lì.
- Scrivilo **chiaro e sintetico**.

## La flessibilità è parte del piano
Ogni parto può prendere strade impreviste: la sicurezza di mamma e bambino viene prima di tutto. Un buon piano del parto include la disponibilità ad **adattarsi**. Averlo pensato, comunque, ti aiuta a sentirti più preparata e ascoltata.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_tiralatte", ARTICLE, PREGNANCY, "Allattamento",
            title = "Tiralatte e conservazione del latte",
            subtitle = "Quando serve e come conservare il latte materno in sicurezza.",
            durationMinutes = 3,
            bodyMarkdown = """
# Tiralatte e conservazione del latte

Il **tiralatte** può essere utile in diverse situazioni: rientro al lavoro, necessità di aumentare o mantenere la produzione, difficoltà temporanee di attacco, o per lasciare il latte quando non puoi esserci.

## Tipi di tiralatte
- **Manuale**: economico, silenzioso, pratico per un uso occasionale.
- **Elettrico**: più rapido, utile per un uso frequente; esistono modelli doppi.

Scegli in base alla frequenza d'uso. L'ostetrica o una consulente possono aiutarti a usarlo bene e a regolare l'intensità per non farti male.

## Conservare il latte in sicurezza
Linee generali (segui le indicazioni del pediatra):
- **Temperatura ambiente**: poche ore.
- **Frigorifero**: alcuni giorni, nella parte più fredda (non nello sportello).
- **Congelatore**: diverse settimane/mesi a seconda del tipo di congelatore.

Conserva in **contenitori puliti** etichettati con la data, in piccole porzioni. Scongela in frigorifero o sotto acqua tiepida, **mai nel microonde** (scalda in modo irregolare e può creare punti bollenti). Il latte scongelato non si ricongela.

## Igiene
Lava bene le mani e i componenti del tiralatte dopo ogni uso. In caso di dubbi su quantità, frequenza o conservazione, chiedi a chi segue te e il bambino.
            """.trimIndent()
        ),
        AcademyContent(
            "pg_vaccini", ARTICLE, PREGNANCY, "Salute",
            title = "Vaccinazioni in gravidanza",
            subtitle = "Perché alcune vaccinazioni proteggono mamma e bambino.",
            durationMinutes = 3,
            bodyMarkdown = """
# Vaccinazioni in gravidanza

Alcune vaccinazioni in gravidanza sono **raccomandate** perché proteggono la mamma e, attraverso il passaggio di anticorpi, anche il **neonato** nei primi mesi, quando è più vulnerabile.

## Perché se ne parla
Il sistema immunitario in gravidanza è leggermente modificato e alcune infezioni possono essere più rischiose. Vaccinarsi nei tempi giusti offre una **protezione mirata** in un momento delicato.

## Come comportarsi
- **Parlane presto** con chi ti segue: sarà il medico a indicarti quali vaccinazioni sono raccomandate, quando farle e quali invece vanno rimandate.
- Verifica anche il tuo **stato vaccinale** prima o all'inizio della gravidanza (alcune vaccinazioni si fanno idealmente **prima** del concepimento).
- Riferisci eventuali allergie o condizioni particolari.

## Messaggio chiave
Le scelte vaccinali in gravidanza sono **personalizzate** e vanno fatte con il medico, sulla base delle raccomandazioni aggiornate. È uno degli argomenti utili da portare già alla **prima visita**.
            """.trimIndent()
        ),

        // ----------------------------- Video gravidanza (ID YouTube verificati) -----------------------------
        AcademyContent("pg_v_primo", VIDEO, PREGNANCY, "Trimestri",
            title = "Sintomi del primo trimestre", subtitle = "Nausee, stanchezza, perdite e altro.",
            durationMinutes = 12, videoId = "MtY0LEHnZow"),
        AcademyContent("pg_v_0_4", VIDEO, PREGNANCY, "Trimestri",
            title = "Gravidanza 0–4 settimane", subtitle = "Quando fare il test e primi sintomi.",
            durationMinutes = 8, videoId = "20vWristVps"),
        AcademyContent("pg_v_5_8", VIDEO, PREGNANCY, "Trimestri",
            title = "Gravidanza 5–8 settimane", subtitle = "Sintomi e sviluppo dell'embrione.",
            durationMinutes = 9, videoId = "WTZQSM279os"),
        AcademyContent("pg_v_nausea", VIDEO, PREGNANCY, "Alimentazione",
            title = "Nausea in gravidanza: soluzioni efficaci", subtitle = "Migliorare la qualità di vita.",
            durationMinutes = 10, videoId = "8frzxJgJ7p4"),
        AcademyContent("pg_v_33_37", VIDEO, PREGNANCY, "Trimestri",
            title = "Gravidanza 33–37 settimane", subtitle = "Preparazione alla nascita.",
            durationMinutes = 9, videoId = "8p0yulwYLck"),
        AcademyContent("pg_v_posizioni", VIDEO, PREGNANCY, "Parto",
            title = "Migliori posizioni per il travaglio", subtitle = "Consigli dell'ostetrica per il dolore.",
            durationMinutes = 13, videoId = "PUmoiR7M7ZQ"),
        AcademyContent("pg_v_respiro", VIDEO, PREGNANCY, "Parto",
            title = "Come respirare in travaglio", subtitle = "Tecniche di respirazione.",
            durationMinutes = 11, videoId = "mc7Py6uk3ks"),
        AcademyContent("pg_v_allatt", VIDEO, PREGNANCY, "Allattamento",
            title = "Allattamento al seno: consigli dell'ostetrica", subtitle = "Anche dopo il cesareo.",
            durationMinutes = 14, videoId = "StkBIk8nkjE"),
        AcademyContent("pg_v_vaccino", VIDEO, PREGNANCY, "Salute",
            title = "Vaccinazioni in gravidanza", subtitle = "Perché parlarne presto.",
            durationMinutes = 5, videoId = "PRIXwv6-VzQ"),
        AcademyContent("pg_v_pavimento", VIDEO, PREGNANCY, "Benessere",
            title = "Esercizi per il pavimento pelvico in gravidanza", subtitle = "Prepararlo dolcemente al parto.",
            durationMinutes = 11, videoId = "xdEbvVmh4FY"),
        AcademyContent("pg_v_perineo", VIDEO, PREGNANCY, "Parto", phases = listOf("Terzo trimestre"),
            title = "Pavimento pelvico e massaggio del perineo", subtitle = "Preparazione al parto.",
            durationMinutes = 12, videoId = "vMd7Yb4d_2s"),
        AcademyContent("pg_v_alim2", VIDEO, PREGNANCY, "Alimentazione",
            title = "Alimentazione in gravidanza: cosa mangiare", subtitle = "I consigli della nutrizionista.",
            durationMinutes = 12, videoId = "NnpGqhxmEck"),
        AcademyContent("pg_v_evitare", VIDEO, PREGNANCY, "Alimentazione",
            title = "Cibi da evitare in gravidanza", subtitle = "Gli alimenti a rischio e perché.",
            durationMinutes = 10, videoId = "uxjioobFVO4")
    )
}
