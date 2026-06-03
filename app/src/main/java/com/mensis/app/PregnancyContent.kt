package com.mensis.app

object PregnancyContent {
    fun headline(summary: PregnancySummary): String = when {
        summary.weeks < 8 -> "Le prime settimane puntano su riposo, idratazione e folati."
        summary.weeks < 14 -> "Sta iniziando la base di organi e placenta: ascolta stanchezza e nausea."
        summary.weeks < 20 -> "Il corpo spesso ritrova piu energia: movimento dolce e routine aiutano."
        summary.weeks < 28 -> "La crescita accelera e conviene curare postura, sonno e visite."
        summary.weeks < 36 -> "Serve piu attenzione a comfort, movimenti fetali e preparazione pratica."
        else -> "Le ultime settimane sono dedicate a monitoraggio, recupero e organizzazione del parto."
    }

    fun weekChecklist(summary: PregnancySummary): List<Pair<String, String>> = when {
        summary.weeks < 12 -> listOf(
            "Priorita" to "Folati, idratazione, sonno, farmaci solo se approvati",
            "Corpo" to "Nausea, seno sensibile e stanchezza sono frequenti",
            "Promemoria" to "Prenota o conferma le prime visite"
        )
        summary.weeks < 28 -> listOf(
            "Priorita" to "Movimento regolare, alimentazione completa e controlli programmati",
            "Corpo" to "Postura, schiena e digestione meritano attenzione",
            "Promemoria" to "Segui le finestre di ecografie ed esami consigliati"
        )
        else -> listOf(
            "Priorita" to "Riposo, comfort, idratazione e borsa/organizzazione parto",
            "Corpo" to "Fiato corto, pressione addominale e sonno piu difficile sono comuni",
            "Promemoria" to "Parla di movimenti fetali, travaglio e segnali da monitorare"
        )
    }

    fun nutritionAdvice(summary: PregnancySummary): List<Pair<String, String>> = buildList {
        add("Schema base" to "Pasti regolari, semplici e vari, senza mangiare per due: proteine, legumi, frutta, verdura, cereali e acqua.")
        add("Acido folico" to "Particolarmente importante soprattutto attorno al concepimento e nel primo trimestre secondo il Ministero della Salute.")
        add("Da evitare" to "Alcol e alimenti che il professionista ti ha sconsigliato; attenzione anche ai cibi a maggior rischio igienico.")
        add("Peso e digestione" to if (summary.weeks >= 28) "Nel terzo trimestre spesso aiutano porzioni piu piccole e frequenti per reflusso e pesantezza." else "Un aumento di peso graduale e controllato resta un obiettivo importante.")
    }

    fun movementAdvice(summary: PregnancySummary): List<Pair<String, String>> = buildList {
        add("Movimento" to "Se la gravidanza e fisiologica e chi ti segue e d'accordo, il movimento moderato e regolare aiuta umore, energia e circolazione.")
        add("Recupero" to "Camminate, mobilita dolce, pause frequenti e sonno curato valgono piu di allenamenti disordinati.")
        add("Corpo" to if (summary.weeks < 28) "Cura postura, respirazione e ascolto della fatica." else "Nel terzo trimestre diventano centrali schiena, gambe, fiato e qualita del riposo.")
    }

    fun appointmentAdvice(summary: PregnancySummary): List<Pair<String, String>> = buildList {
        add("Visite ed esami" to "Il SSN prevede controlli ed esami utili alla gravidanza fisiologica: tienili ordinati e visibili.")
        add("Domande da portare" to "Segna dubbi su sintomi, farmaci, vaccini, movimenti fetali, lavoro, parto e allattamento.")
        add("Documenti" to if (summary.weeks < 28) "Conserva referti, esami e calendario dei controlli nello stesso posto." else "Nelle ultime settimane prepara anche contatti, documenti e piano pratico per il parto.")
    }

    fun protectionAdvice(summary: PregnancySummary): List<Pair<String, String>> = buildList {
        add("Fumo e alcol" to "Durante la gravidanza e importante evitare fumo e alcol per proteggere lo sviluppo del bambino.")
        add("Vaccinazioni" to "Le vaccinazioni raccomandate in gravidanza vanno discusse con i professionisti che ti seguono.")
        add("Caldo e stagione" to if (summary.weeks >= 24) "Con il caldo servono piu acqua, pause, ombra e orari freschi." else "Anche fuori estate, idratazione e ritmi regolari aiutano molto.")
        add("Salute mentale" to "Se ansia, tristezza o sovraccarico diventano forti, chiedere aiuto presto fa parte della cura.")
    }

    fun monthFromWeeks(weeks: Int): Int = ((weeks / 4) + 1).coerceIn(1, 9)

    fun monthTitle(month: Int): String = "Mese $month"

    fun monthOverview(month: Int): List<Pair<String, String>> = when (month) {
        1 -> listOf(
            "Nel corpo" to "Possono comparire stanchezza, seno sensibile e bisogno di rallentare.",
            "Per il bimbo" to "Si avvia l'impianto e iniziano le basi dello sviluppo embrionale.",
            "Obiettivo" to "Riposo, acido folico, idratazione e conferma del percorso di cura."
        )
        2 -> listOf(
            "Nel corpo" to "Nausea, sonnolenza e odori più intensi sono molto comuni.",
            "Per il bimbo" to "Inizia una fase rapida di organizzazione di organi e tessuti.",
            "Obiettivo" to "Proteggere energie e alimentazione anche se l'appetito cambia."
        )
        3 -> listOf(
            "Nel corpo" to "Le nausee possono continuare, ma il quadro spesso diventa più chiaro.",
            "Per il bimbo" to "La crescita resta veloce e il primo trimestre si conclude.",
            "Obiettivo" to "Tenere ordinati esami, sintomi e domande da portare in visita."
        )
        4 -> listOf(
            "Nel corpo" to "Molte persone sentono un po' più di energia e stabilità.",
            "Per il bimbo" to "La gravidanza entra nella fase di crescita più visibile.",
            "Obiettivo" to "Riprendere routine, movimento dolce e pasti regolari."
        )
        5 -> listOf(
            "Nel corpo" to "Postura, schiena e digestione iniziano a chiedere più attenzione.",
            "Per il bimbo" to "La crescita prosegue e il corpo cambia equilibrio.",
            "Obiettivo" to "Curare sonno, postura e qualità dell'alimentazione."
        )
        6 -> listOf(
            "Nel corpo" to "Possono aumentare fame, reflusso, pesantezza o gambe stanche.",
            "Per il bimbo" to "Il secondo trimestre è avanzato e i controlli diventano centrali.",
            "Obiettivo" to "Monitorare comfort quotidiano e continuità del diario."
        )
        7 -> listOf(
            "Nel corpo" to "Il volume addominale pesa di più su sonno, respiro e mobilità.",
            "Per il bimbo" to "Si entra nel terzo trimestre e la preparazione si fa concreta.",
            "Obiettivo" to "Ridurre sovraccarico, organizzare casa e ascoltare il corpo."
        )
        8 -> listOf(
            "Nel corpo" to "Fiato corto, stanchezza e difficoltà a dormire possono aumentare.",
            "Per il bimbo" to "La crescita è intensa e i movimenti diventano un riferimento importante.",
            "Obiettivo" to "Parlare di segnali, travaglio, allattamento e ultime visite."
        )
        else -> listOf(
            "Nel corpo" to "Serve molta gestione del comfort e del recupero quotidiano.",
            "Per il bimbo" to "Le ultime settimane sono orientate alla maturazione finale.",
            "Obiettivo" to "Tenere pronti documenti, contatti, borsa e segnali da monitorare."
        )
    }

    fun monthFoods(month: Int): List<Pair<String, String>> = when (month) {
        1, 2, 3 -> listOf(
            "Da privilegiare" to "Pasti piccoli ma frequenti, cibi semplici, fonti di folati, legumi, frutta e acqua.",
            "Se c'è nausea" to "Cracker secchi, sapori delicati e pause regolari possono aiutare.",
            "Da evitare" to "Alcol e alimenti che il professionista ti ha sconsigliato o a rischio igienico."
        )
        4, 5, 6 -> listOf(
            "Da privilegiare" to "Proteine, fibre, calcio, ferro e idratazione costante.",
            "Per la digestione" to "Mangiare con più regolarità aiuta energia e stabilità.",
            "Da limitare" to "Eccessi che peggiorano gonfiore, reflusso o stanchezza."
        )
        else -> listOf(
            "Da privilegiare" to "Pasti più piccoli e frequenti, acqua distribuita nella giornata, fibre e proteine.",
            "Per il reflusso" to "Può aiutare cenare prima e alleggerire i pasti molto ricchi.",
            "Da limitare" to "Ciò che accentua reflusso, pesantezza o scarsa qualità del sonno."
        )
    }

    fun monthVisits(month: Int): List<Pair<String, String>> = when (month) {
        1, 2, 3 -> listOf(
            "Visite" to "Conferma la presa in carico e chiarisci farmaci, integratori e primi esami.",
            "Domande utili" to "Sintomi, nausea, lavoro, sport, rapporti, folati e calendario controlli.",
            "Documenti" to "Apri una raccolta unica per referti, date e note."
        )
        4, 5, 6 -> listOf(
            "Visite" to "Ecografie ed esami di routine vanno tenuti ben visibili nel calendario.",
            "Domande utili" to "Peso, movimento, schiena, digestione, viaggio e vaccinazioni.",
            "Documenti" to "Tieni traccia delle domande emerse tra una visita e l'altra."
        )
        else -> listOf(
            "Visite" to "Le ultime settimane richiedono organizzazione più pratica e monitoraggio.",
            "Domande utili" to "Movimenti fetali, segnali del travaglio, parto, allattamento e rientro a casa.",
            "Documenti" to "Prepara contatti, documenti, piano logistico e ciò che ti serve per il parto."
        )
    }

    fun monthChecklist(month: Int): List<Pair<String, String>> = when (month) {
        1 -> listOf("Checklist" to "Acido folico, acqua, riposo, primo contatto con i professionisti.")
        2 -> listOf("Checklist" to "Gestisci nausea e stanchezza senza forzarti.")
        3 -> listOf("Checklist" to "Raccogli esami, sintomi e dubbi del primo trimestre.")
        4 -> listOf("Checklist" to "Riporta nel diario energia, umore, sonno e attività.")
        5 -> listOf("Checklist" to "Osserva postura, schiena, fame e digestione.")
        6 -> listOf("Checklist" to "Conferma esami e alleggerisci ciò che aumenta il reflusso.")
        7 -> listOf("Checklist" to "Organizza spazi, riposo e materiali utili.")
        8 -> listOf("Checklist" to "Parla di travaglio, movimenti fetali e allattamento.")
        else -> listOf("Checklist" to "Tieni pronti documenti, contatti e segnali da monitorare.")
    }

    fun monthWellbeing(month: Int): List<Pair<String, String>> = when (month) {
        1, 2, 3 -> listOf(
            "Riposo" to "Nei primi mesi il recupero conta più della performance.",
            "Diario utile" to "Annota nausea, sonno, idratazione e odori/sensibilità che cambiano."
        )
        4, 5, 6 -> listOf(
            "Routine" to "Sfrutta il periodo più stabile per consolidare sonno, pasti e movimento dolce.",
            "Diario utile" to "Segna energia, digestione, postura, schiena e qualità dell'umore."
        )
        else -> listOf(
            "Comfort" to "Nel terzo trimestre tutto ruota più intorno a respirazione, sonno e gestione del peso addominale.",
            "Diario utile" to "Segna gonfiore, sonno, fiato, comfort e domande per parto/allattamento."
        )
    }

    fun libraryEntries(): List<PregnancyLibraryEntry> = listOf(
        PregnancyLibraryEntry(
            id = "warning_signs",
            month = 9,
            type = "Guida",
            title = "Segnali d'allarme da non ignorare",
            subtitle = "Quando chiedere aiuto subito durante la gravidanza",
            source = "CDC / adattamento informativo",
            sourceUrl = "https://www.cdc.gov/hearher/maternal-warning-signs/index.html",
            sections = listOf(
                "Da non rimandare" to "Dolore toracico, difficoltà a respirare, convulsioni, forte mal di testa o sanguinamento importante richiedono attenzione immediata.",
                "Movimenti e benessere" to "Se percepisci un cambiamento importante rispetto ai movimenti del bambino o ti senti improvvisamente molto peggio, contatta subito chi ti segue.",
                "Salute mentale" to "Pensieri di farti del male, disperazione intensa o forte agitazione non vanno tenuti per sé: chiedere aiuto subito fa parte della cura."
            )
        ),
        PregnancyLibraryEntry(
            id = "mese1_guida_generale",
            month = 1,
            type = "Articolo",
            title = "Primi passi della gravidanza",
            subtitle = "Come impostare bene il percorso fin dall'inizio",
            source = "Ministero della Salute",
            sourceUrl = "https://www.salute.gov.it/new/it/tema/salute-della-donna/gravidanza-0/",
            sections = listOf(
                "Cosa conta adesso" to "All'inizio servono soprattutto conferma del percorso di cura, folati, idratazione e ascolto della stanchezza.",
                "Segnali frequenti" to "Nausea, seno sensibile, sonno e odori più intensi possono comparire presto.",
                "Organizzazione" to "Apri subito un archivio unico per esami, domande e date importanti."
            )
        ),
        PregnancyLibraryEntry(
            id = "mese2_video_vaccino",
            month = 2,
            type = "Video",
            title = "Vaccinazione in gravidanza",
            subtitle = "Pillola video: perché parlarne presto protegge mamma e neonato",
            source = "Bambino Gesu",
            sourceUrl = "https://www.ospedalebambinogesu.it/bambino/pediatria-in-pillole/video-pillole/page/12",
            youtubeId = "PRIXwv6-VzQ",
            sections = listOf(
                "Idea chiave" to "Le vaccinazioni raccomandate in gravidanza vanno affrontate con i professionisti che ti seguono, non all'ultimo.",
                "Cosa chiarire" to "Tempistiche, benefici per mamma e bambino, eventuali dubbi sui vaccini indicati.",
                "Come usarlo" to "Porta questo tema nella prossima visita e annota le risposte nel diario."
            )
        ),
        PregnancyLibraryEntry(
            id = "mese3_art_visite",
            month = 3,
            type = "Articolo",
            title = "Visite ed esami in gravidanza",
            subtitle = "Prestazioni gratuite e controlli utili nel percorso",
            source = "Ministero della Salute",
            sourceUrl = "https://www.salute.gov.it/new/it/tema/salute-della-donna/visite-ed-esami-gravidanza/",
            sections = listOf(
                "Cosa sapere" to "Alla fine del primo trimestre il calendario dei controlli deve essere chiaro e ordinato.",
                "Perché è utile" to "Sapere cosa è previsto riduce ansia e dimenticanze.",
                "Azioni pratiche" to "Segna date, referti mancanti e dubbi da portare in visita."
            )
        ),
        PregnancyLibraryEntry(
            id = "mese4_art_trimestri",
            month = 4,
            type = "Articolo",
            title = "Secondo trimestre: come cambia il ritmo",
            subtitle = "Energia, routine e abitudini da consolidare",
            source = "Ministero della Salute",
            sourceUrl = "https://www.salute.gov.it/new/it/tema/salute-del-bambino-e-delladolescente/secondo-e-terzo-trimestre-di-gravidanza/",
            sections = listOf(
                "Transizione" to "Nel quarto mese molte persone ritrovano un po' di equilibrio e possono curare meglio la routine.",
                "Su cosa concentrarsi" to "Pasti regolari, sonno, movimento moderato e continuità del diario.",
                "Attenzione" to "Se qualcosa cambia molto rispetto al solito, annotalo e portalo in visita."
            )
        ),
        PregnancyLibraryEntry(
            id = "mese5_art_folico",
            month = 5,
            type = "Articolo",
            title = "Acido folico e nutrienti: richiamo utile",
            subtitle = "Perché parlare di integrazione e qualità della dieta resta importante",
            source = "Ministero della Salute",
            sourceUrl = "https://www.salute.gov.it/new/it/social-network/acido-folico-perche-e-cosi-importante/",
            sections = listOf(
                "Idea chiave" to "La qualità dell'alimentazione conta per tutta la gravidanza, non solo all'inizio.",
                "Da controllare" to "Confronta integratori, dieta reale e indicazioni dei professionisti.",
                "Nel diario" to "Segna energia, fame, digestione e idratazione: aiutano a leggere meglio i tuoi ritmi."
            )
        ),
        PregnancyLibraryEntry(
            id = "mese6_video_allattamento",
            month = 6,
            type = "Video",
            title = "Allattamento: consigli pratici già in gravidanza",
            subtitle = "Pillola video da vedere prima del terzo trimestre finale",
            source = "Bambino Gesu",
            sourceUrl = "https://www.ospedalebambinogesu.it/allattamento-al-seno-consigli-pratici-105608/",
            youtubeId = "GyDf_9vk6hk",
            sections = listOf(
                "Perché adesso" to "Informarsi in anticipo rende l'allattamento meno improvvisato.",
                "Cosa portarti via" to "Aspettative realistiche, sostegno, comfort e prime domande da fare.",
                "Azione" to "Apri una nota dedicata a parto e allattamento con i temi che vuoi chiarire."
            )
        ),
        PregnancyLibraryEntry(
            id = "mese7_art_caldo",
            month = 7,
            type = "Articolo",
            title = "Caldo e gravidanza",
            subtitle = "Come gestire sete, fiato, stanchezza e ritmo della giornata",
            source = "Ministero della Salute",
            sourceUrl = "https://www.salute.gov.it/new/it/prodotti-editoriali/estate-sicura-come-vincere-il-caldo-gravidanza-0/",
            sections = listOf(
                "Idea chiave" to "Con il terzo trimestre il comfort termico pesa di più su energia e recupero.",
                "Cosa aiuta" to "Acqua, pause, ombra, orari più freschi e abiti comodi.",
                "Quando annotarlo" to "Se il caldo peggiora sonno, gonfiore o stanchezza, segnalo nel diario."
            )
        ),
        PregnancyLibraryEntry(
            id = "mese8_video_pertosse",
            month = 8,
            type = "Video",
            title = "Pertosse in gravidanza",
            subtitle = "Perché il vaccino viene discusso proprio in questa fase",
            source = "Bambino Gesu",
            sourceUrl = "https://www.ospedalebambinogesu.it/il-vaccino-contro-la-pertosse-in-gravidanza---intervista-al-dott-tozzi-104234/",
            youtubeId = "PRIXwv6-VzQ",
            sections = listOf(
                "Messaggio centrale" to "Nel terzo trimestre avanzato va chiarito bene il tema della protezione del neonato.",
                "Da chiedere" to "Tempistiche, benefici attesi e come si inserisce nel tuo percorso reale.",
                "Azione" to "Segna la domanda per la prossima visita se non l'hai ancora affrontata."
            )
        ),
        PregnancyLibraryEntry(
            id = "mese9_art_risorse_finali",
            month = 9,
            type = "Articolo",
            title = "Fine gravidanza: segnali e organizzazione",
            subtitle = "Ultime settimane tra monitoraggio, comfort e preparazione pratica",
            source = "ISS / Ministero",
            sourceUrl = "https://www.epicentro.iss.it/materno/pdf/SNLG%201_2025%20Gravidanza-fisiologica%20Parte-2.pdf",
            sections = listOf(
                "Cosa conta" to "Ridurre caos, avere chiari contatti, segnali e logistica rende l'ultima fase più sostenibile.",
                "Da tenere pronto" to "Documenti, borsa, contatti, percorso per il parto e temi sull'allattamento.",
                "Nel diario" to "Segna comfort, movimenti percepiti, sonno, ansia e domande dell'ultima fase."
            )
        )
    )

    fun entriesForMonth(month: Int): List<PregnancyLibraryEntry> = libraryEntries().filter { it.month == month }

    fun entryById(id: String): PregnancyLibraryEntry? = libraryEntries().firstOrNull { it.id == id }
}
