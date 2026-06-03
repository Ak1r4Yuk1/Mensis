package com.mensis.app

/** Dati di sviluppo fetale settimana per settimana — tutto incluso nell'app, niente fonti esterne. */
data class BabyWeek(
    val week: Int,
    val fruit: String,
    val lengthCm: Double,
    val weightG: Int,
    val highlight: String
)

object BabyDevelopment {
    const val MAX_WEEK = 40
    private const val MAX_LENGTH = 51.2

    val weeks: List<BabyWeek> = listOf(
        BabyWeek(0, "", 0.0, 0, "Inizio del conteggio dall'ultima mestruazione. Il concepimento avverrà tra circa due settimane."),
        BabyWeek(1, "", 0.0, 0, "Il corpo si prepara all'ovulazione: l'endometrio si ispessisce per accogliere un eventuale embrione."),
        BabyWeek(2, "", 0.0, 0, "Avviene l'ovulazione: se l'ovulo incontra uno spermatozoo, c'è il concepimento."),
        BabyWeek(3, "", 0.0, 0, "L'ovulo fecondato (zigote) si divide e viaggia verso l'utero per impiantarsi."),
        BabyWeek(4, "un seme di papavero", 0.1, 0, "Si completa l'impianto; iniziano a formarsi placenta e sacco amniotico."),
        BabyWeek(5, "un seme di sesamo", 0.2, 0, "Si forma il tubo neurale, base di cervello e midollo spinale."),
        BabyWeek(6, "una lenticchia", 0.6, 0, "Il cuoricino inizia a battere; spuntano gli abbozzi di braccia e gambe."),
        BabyWeek(7, "un mirtillo", 1.3, 1, "Si sviluppano testa, occhi e narici; il cervello cresce in fretta."),
        BabyWeek(8, "un lampone", 1.6, 1, "Si formano le dita di mani e piedi; tutti gli organi principali sono avviati."),
        BabyWeek(9, "una ciliegia", 2.3, 2, "Il cuore ha quattro camere; compaiono i primi movimenti spontanei."),
        BabyWeek(10, "un dattero", 3.1, 4, "Finita la fase embrionale: ora è un feto. Iniziano a formarsi le unghie."),
        BabyWeek(11, "un fico", 4.1, 7, "Si sviluppano i denti da latte sotto le gengive; la testa è ancora grande."),
        BabyWeek(12, "un lime", 5.4, 14, "Compaiono i riflessi; i reni iniziano a produrre urina."),
        BabyWeek(13, "un baccello di piselli", 7.4, 23, "Si formano le corde vocali; il corpo cresce in proporzione alla testa."),
        BabyWeek(14, "un limone", 8.7, 43, "La pelle si ispessisce e compare una peluria fine (lanugo)."),
        BabyWeek(15, "una mela", 10.1, 70, "Percepisce la luce attraverso le palpebre chiuse; le ossa si rafforzano."),
        BabyWeek(16, "un avocado", 11.6, 100, "I muscoli facciali si muovono: può già fare smorfie."),
        BabyWeek(17, "una pera", 13.0, 140, "Si forma il grasso bruno; lo scheletro passa da cartilagine a osso."),
        BabyWeek(18, "un peperone", 14.2, 190, "Inizia a percepire i suoni; le impronte digitali sono formate."),
        BabyWeek(19, "un pomodoro cuore di bue", 15.3, 240, "Si forma la vernice caseosa che protegge la pelle nel liquido amniotico."),
        BabyWeek(20, "una banana", 16.4, 300, "Metà percorso! Potresti iniziare a sentire i primi movimenti."),
        BabyWeek(21, "una carota", 26.7, 360, "Coordina meglio i movimenti; crescono ciglia e sopracciglia."),
        BabyWeek(22, "una zucchina", 27.8, 430, "Sviluppa il senso del tatto; i tratti del viso sono ben definiti."),
        BabyWeek(23, "un mango", 28.9, 501, "I polmoni si preparano alla respirazione; sviluppa l'equilibrio."),
        BabyWeek(24, "una pannocchia", 30.0, 600, "Le palpebre si aprono; reagisce a luce e suoni."),
        BabyWeek(25, "una rapa", 34.6, 660, "Si forma il grasso sottocutaneo; la pelle è meno trasparente."),
        BabyWeek(26, "una lattuga", 35.6, 760, "I polmoni producono surfattante; risponde di più agli stimoli."),
        BabyWeek(27, "un cavolfiore", 36.6, 875, "Inizia il terzo trimestre; cicli sonno-veglia più regolari."),
        BabyWeek(28, "una melanzana", 37.6, 1005, "Apre e chiude gli occhi; il cervello cresce e si organizza molto."),
        BabyWeek(29, "una zucca butternut", 38.6, 1153, "Muscoli e polmoni continuano a maturare; movimenti più decisi."),
        BabyWeek(30, "un cavolo cappuccio", 39.9, 1319, "Il cervello forma le tipiche pieghe; regola meglio la temperatura."),
        BabyWeek(31, "un cocco", 41.1, 1502, "Tutti i sensi funzionano; rapido aumento di peso e grasso."),
        BabyWeek(32, "un gambo di sedano", 42.4, 1702, "Le unghie raggiungono la punta delle dita; spesso si gira a testa in giù."),
        BabyWeek(33, "un ananas", 43.7, 1918, "Le ossa si induriscono (tranne il cranio); le pupille reagiscono alla luce."),
        BabyWeek(34, "un melone cantalupo", 45.0, 2146, "Sistema nervoso e polmoni quasi del tutto maturi."),
        BabyWeek(35, "un melone", 46.2, 2383, "Reni completamente sviluppati; il fegato elabora le scorie."),
        BabyWeek(36, "una lattuga romana", 47.4, 2622, "Perde gran parte della lanugo; guadagna circa 200 g a settimana."),
        BabyWeek(37, "una bietola", 48.6, 2859, "'Termine precoce': i polmoni completano gli ultimi affinamenti."),
        BabyWeek(38, "un porro", 49.8, 3083, "Accumula riserve di grasso; afferra con forza, organi pronti."),
        BabyWeek(39, "un'anguria piccola", 50.7, 3288, "A termine: cervello e polmoni maturano fino alla nascita."),
        BabyWeek(40, "una zucca piccola", 51.2, 3462, "A termine pieno: è pronto a nascere. In bocca al lupo!")
    )

    fun forWeek(week: Int): BabyWeek {
        val w = week.coerceIn(0, MAX_WEEK)
        return weeks.lastOrNull { it.week <= w } ?: weeks.first()
    }

    /** Frazione di crescita 0..1 per la visualizzazione che cresce. */
    fun growthFraction(week: Int): Float =
        (forWeek(week).lengthCm / MAX_LENGTH).toFloat().coerceIn(0.04f, 1f)

    fun overallProgress(week: Int): Float = (week.coerceIn(0, MAX_WEEK).toFloat() / MAX_WEEK)
}
