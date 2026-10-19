package com.example.data.catalog

data class PlantaDaninha(
    val id: String,
    val nomeComum: String,
    val nomeCientifico: String,
    val classe: String, // Gramínea, Folha Larga, Ciperácea
    val reproducao: String, // Sementes, Rizomas, Tubérculos, etc.
    val caracteristicas: String,
    val riscoResistencia: String,
    val manejoSugerido: String
)

data class PragaAgricola(
    val id: String,
    val nomeComum: String,
    val nomeCientifico: String,
    val cultura: String,
    val grupo: String, // Lagarta, Percevejo, Sugador, Besouro, Broca
    val sintomasDano: String,
    val metodoAmostragem: String,
    val nivelAcaoEmbrapa: String,
    val inimigosNaturais: String,
    val manejoRecomendado: String
)

data class InimigoNatural(
    val nome: String,
    val tipo: String, // Predador, Parasitoide
    val alvosPrincipais: String,
    val importancia: String
)

data class DiagnosticoSintoma(
    val problema: String, // Deficiência, Doença, Praga, Herbicida, Estresse
    val aparenciaVisual: String,
    val distribuicaoNoCampo: String,
    val acaoImediata: String
)

data class ChecklistItem(
    val fase: String, // Antes do Plantio, Emergência, Vegetativo, Reprodutivo, Colheita
    val acoes: List<String>
)

data class ErroComum(
    val titulo: String,
    val impacto: String,
    val solucaoCorreta: String
)

object AgronomicData {

    val plantasDaninhas = listOf(
        PlantaDaninha(
            id = "amargoso",
            nomeComum = "Capim-amargoso",
            nomeCientifico = "Digitaria insularis",
            classe = "Gramínea (Poaceae)",
            reproducao = "Sementes aladas e rizomas perenes",
            caracteristicas = "Colmos eretos, folhas lanceoladas com bainha pilosa, inflorescência plumosa. Forma touceiras densas.",
            riscoResistencia = "Alto relato de resistência a Glifosato e inibidores de ACCase (graminicidas).",
            manejoSugerido = "Dessecação sequencial antes do perfilhamento ou formação de rizomas. Rotação de princípios ativos, palhada espessa e limpeza de colheitadeiras."
        ),
        PlantaDaninha(
            id = "buva",
            nomeComum = "Buva",
            nomeCientifico = "Conyza bonariensis / canadensis / sumatrensis",
            classe = "Folha Larga (Eudicotiledônea)",
            reproducao = "Sementes leves e abundantes dispersas pelo vento",
            caracteristicas = "Haste ereta e densamente pubescente, folhas alternadas serrilhadas, floresce em capítulos pequenos.",
            riscoResistencia = "Múltipla resistência confirmada (Glifosato, ALS, 2,4-D em biótipos pontuais).",
            manejoSugerido = "Controle nas fases de plântula ou roseta (< 10 cm). Manejo na entressafra com consórcio braquiária e pré-emergentes residuais."
        ),
        PlantaDaninha(
            id = "pe_de_galinha",
            nomeComum = "Capim-pé-de-galinha",
            nomeCientifico = "Eleusine indica",
            classe = "Gramínea (Poaceae)",
            reproducao = "Sementes com alta taxa de emergência em solo descompactado",
            caracteristicas = "Planta cespitosa com colmos achatados na base, espigas digitadas dispostas como pés de galinha.",
            riscoResistencia = "Resistência a Glifosato e casos a inibidores de ACCase.",
            manejoSugerido = "Aplicações precoces com graminicidas alternados, uso de pré-emergentes e fechamento rápido das entrelinhas."
        ),
        PlantaDaninha(
            id = "caruru",
            nomeComum = "Caruru",
            nomeCientifico = "Amaranthus palmeri / hybridus / retroflexus",
            classe = "Folha Larga (Eudicotiledônea)",
            reproducao = "Produção maciça de sementes (até 500 mil por fêmea em A. palmeri)",
            caracteristicas = "Crescimento extremamente veloz, pecíolos longos com folhas ovaladas e espinhos no caso do espinhoso.",
            riscoResistencia = "Casos graves de múltipla resistência (Glifosato + ALS).",
            manejoSugerido = "Monitoramento intensivo de escapes, arranque manual pré-frutificação, herbicidas pré-emergentes com residual comprovado."
        ),
        PlantaDaninha(
            id = "picao_preto",
            nomeComum = "Picão-preto",
            nomeCientifico = "Bidens pilosa / subalternans",
            classe = "Folha Larga (Eudicotiledônea)",
            reproducao = "Aquênios farpados aderentes a roupas, pelos e animais",
            caracteristicas = "Haste quadrangular ramificada, folhas compostas, flores amarelas marginais com centro escuro.",
            riscoResistencia = "Resistência relatada a inibidores da ALS em várias regiões do Brasil.",
            manejoSugerido = "Rotação de mecanismos de ação (inibidores de Protox, auxinas sintéticas), rotação com culturas de cobertura."
        ),
        PlantaDaninha(
            id = "tiririca",
            nomeComum = "Tiririca",
            nomeCientifico = "Cyperus rotundus",
            classe = "Ciperácea (Cyperaceae)",
            reproducao = "Tubérculos e bulbos interligados por rede subterrânea",
            caracteristicas = "Colmo de seção triangular característico, folhas verde-escuras brilhantes em roseta basal trística.",
            riscoResistencia = "Altíssima tolerância física devido à dormência dos tubérculos.",
            manejoSugerido = "Controle integrado com roçadas estratégicas, herbicidas específicos translocáveis na fase de brotação ativa (4 a 6 folhas)."
        ),
        PlantaDaninha(
            id = "corda_de_viola",
            nomeComum = "Corda-de-viola",
            nomeCientifico = "Ipomoea grandifolia / purpurea / triloba",
            classe = "Folha Larga (Eudicotiledônea)",
            reproducao = "Sementes duras com dormência tegumentar prolongada",
            caracteristicas = "Hábito trepador/volúvel, enrola-se nas hastes das culturas provocando embaraço grave na colhedora.",
            riscoResistencia = "Tolerância natural a baixas doses de glifosato.",
            manejoSugerido = "Manejo com herbicidas auxínicos e inibidores de carotenóides, controle antes do enrolamento nas plantas."
        ),
        PlantaDaninha(
            id = "trapoeraba",
            nomeComum = "Trapoeraba",
            nomeCientifico = "Commelina benghalensis / erecta",
            classe = "Folha Larga (Commelinaceae)",
            reproducao = "Sementes aéreas, sementes subterrâneas e fragmentos de caule",
            caracteristicas = "Caules carnosos prostrados ou ascendentes que enraízam nos nós; flores azuis vistosas.",
            riscoResistencia = "Alta tolerância natural ao glifosato e rebrota rápida.",
            manejoSugerido = "Aplicação de herbicidas associados (carfentrazone, 2,4-D) em plantas sem estresse, manejo no plantio direto."
        ),
        PlantaDaninha(
            id = "marmelada",
            nomeComum = "Capim-marmelada / Papuã",
            nomeCientifico = "Urochloa plantaginea",
            classe = "Gramínea (Poaceae)",
            reproducao = "Sementes vigorosas com banco persistente",
            caracteristicas = "Folhas largas verde-claras brilhantes, nós avermelhados, colmos suculentos.",
            riscoResistencia = "Relatos de biótipos resistentes a inibidores de ALS.",
            manejoSugerido = "Fechamento rápido de entrelinhas, dessecação pré-plantio e rotação de graminicidas na cultura da soja."
        ),
        PlantaDaninha(
            id = "azevem",
            nomeComum = "Azevém",
            nomeCientifico = "Lolium multiflorum",
            classe = "Gramínea (Poaceae)",
            reproducao = "Sementes em pastagens de inverno",
            caracteristicas = "Folhas estreitas verde-brilhantes, bainha avermelhada na base, espiga delgada.",
            riscoResistencia = "Altos casos de resistência a Glifosato e ACCase na Região Sul.",
            manejoSugerido = "Manejo antecipado na dessecação de inverno e rotação rigorosa de princípios ativos."
        )
    )

    val pragasAgricolas = listOf(
        PragaAgricola(
            id = "percevejo_marrom",
            nomeComum = "Percevejo-marrom",
            nomeCientifico = "Euschistus heros",
            cultura = "Soja",
            grupo = "Percevejo Sugador",
            sintomasDano = "Pica vagens e grãos em formação, causando abortamento, sementes chochas e retenção foliar (soja louca).",
            metodoAmostragem = "Pano de batida (1 m) entre duas linhas em 6 a 10 pontos por talhão.",
            nivelAcaoEmbrapa = "Grão comercial: 2 percevejos/metro de pano de batida. Campo de sementes: 1 percevejo/metro.",
            inimigosNaturais = "Micro-himenópteros parasitoides de ovos (Telenomus podisi e Trissolcus basalis).",
            manejoRecomendado = "Iniciar monitoramento a partir de R3. Evitar pulverização desnecessária para preservar parasitoides de ovos."
        ),
        PragaAgricola(
            id = "lagarta_soja",
            nomeComum = "Lagarta-da-soja",
            nomeCientifico = "Anticarsia gemmatalis",
            cultura = "Soja",
            grupo = "Lagarta Desfolhadora",
            sintomasDano = "Raspagem inicial e consumo voraz de limbo foliar, preservando as nervuras principais.",
            metodoAmostragem = "Pano de batida medindo lagartas maiores que 1,5 cm e percentual visual de desfolha.",
            nivelAcaoEmbrapa = "Fase vegetativa: 30% desfolha OU 20 lagartas grandes (>1,5 cm) por pano. Reprodutivo (R1 a R6): 15% desfolha.",
            inimigosNaturais = "Fungo Nomuraea rileyi, vírus Baculovirus anticarsia, predadores (joaninhas e tesourinhas).",
            manejoRecomendado = "Uso de biológicos (Baculovirus, Bt) em lagartas pequenas e inseticidas fisiológicos reguladores de crescimento."
        ),
        PragaAgricola(
            id = "falsa_medideira",
            nomeComum = "Falsa-medideira",
            nomeCientifico = "Chrysodeixis includens",
            cultura = "Soja",
            grupo = "Lagarta Desfolhadora",
            sintomasDano = "Desfolha do terço médio e inferior da planta, deixando aspecto rendilhado característico nas folhas.",
            metodoAmostragem = "Pano de batida sacudindo vigorosamente as plantas até o baixeiro.",
            nivelAcaoEmbrapa = "20 lagartas (>1,5 cm) por pano ou 30% desfolha (vegetativo) / 15% desfolha (reprodutivo).",
            inimigosNaturais = "Trichogramma pretiosum, micro-vespas parasitoides e fungos entomopatogênicos.",
            manejoRecomendado = "Atenção à tecnologia da pulverização (penetração no baixeiro) e produtos específicos com seletividade."
        ),
        PragaAgricola(
            id = "lagarta_cartucho_milho",
            nomeComum = "Lagarta-do-cartucho",
            nomeCientifico = "Spodoptera frugiperda",
            cultura = "Milho",
            grupo = "Lagarta de Cartucho",
            sintomasDano = "Raspagem foliar inicial com pequenas janelas, perfuração do cartucho e fezes úmidas bloqueando o meristema.",
            metodoAmostragem = "Inspeção visual direta de 100 a 200 plantas por talhão divididas em 5 a 10 pontos de 20 plantas.",
            nivelAcaoEmbrapa = "10% a 20% de plantas com notas 1 a 3 na escala Davis (sintomas iniciais antes da penetração no cartucho).",
            inimigosNaturais = "Tesourinha (Doru luteipes), Telenomus remus, Trichogramma e agentes entomopatogênicos.",
            manejoRecomendado = "Plantio de refúgio estruturado em milho Bt (mínimo 10% da área), pulverização direcionada nas primeiras fases."
        ),
        PragaAgricola(
            id = "cigarrinha_milho",
            nomeComum = "Cigarrinha-do-milho",
            nomeCientifico = "Dalbulus maidis",
            cultura = "Milho",
            grupo = "Sugador / Vetor",
            sintomasDano = "Transmissão dos molicutes do enfezamento pálido e vermelho, e do vírus do raiado fino. Causa superbrotamento e quebramento.",
            metodoAmostragem = "Contagem visual no cartucho das plantas pela manhã nas fases VE a V8.",
            nivelAcaoEmbrapa = "Presença de 1 cigarrinha por planta no cartucho nas primeiras semanas já exige intervenção rigorosa.",
            inimigosNaturais = "Aranhas e percevejos predadores generalistas.",
            manejoRecomendado = "Eliminação total de milho tiguera (ponte verde), sincronização de época de semeadura, tratamento industrial de sementes e híbridos tolerantes."
        ),
        PragaAgricola(
            id = "mosca_branca",
            nomeComum = "Mosca-branca",
            nomeCientifico = "Bemisia tabaci",
            cultura = "Feijão / Soja / Algodão",
            grupo = "Sugador / Vetor",
            sintomasDano = "Sucção de seiva, injeção de toxinas, transmissão do mosaico dourado e excreção de melaço com fumagina.",
            metodoAmostragem = "Batida de ponteiros em bandeja espelhada ou contagem de ninfas no terço médio/inferior.",
            nivelAcaoEmbrapa = "Feijão: presença de adultos na fase inicial (V1 a V3) exige controle imediato para prevenir virose.",
            inimigosNaturais = "Parasitoides Encarsia e Eretmocerus, fungo Beauveria bassiana.",
            manejoRecomendado = "Barreira vegetal, cultivares com resistência ao mosaico, inseticidas fisiológicos e biológicos."
        ),
        PragaAgricola(
            id = "bicudo_algodoeiro",
            nomeComum = "Bicudo-do-algodoeiro",
            nomeCientifico = "Anthonomus grandis",
            cultura = "Algodão",
            grupo = "Coleóptero / Curculionídeo",
            sintomasDano = "Perfurações de alimentação e postura em botões florais e maçãs, causando queda prematura das estruturas reprodutivas.",
            metodoAmostragem = "Armadilhas de feromônio antes do plantio; amostragem de 100 botões florais por talhão.",
            nivelAcaoEmbrapa = "5% de botões florais atacados no início da frutificação.",
            inimigosNaturais = "Parasitoides de larvas como Catolaccus grandis.",
            manejoRecomendado = "Destruição rigorosa de soqueiras na entressafra, vazio sanitário do algodoeiro e aplicações em bordadura no início."
        ),
        PragaAgricola(
            id = "broca_cafe",
            nomeComum = "Broca-do-café",
            nomeCientifico = "Hypothenemus hampei",
            cultura = "Café",
            grupo = "Coleóptero / Broca",
            sintomasDano = "Perfura os frutos na região da coroa, destrói as sementes e causa queda e depreciação da bebida.",
            metodoAmostragem = "Amostragem de 100 frutos de café na época do 'trânsito da broca' (cerca de 90 dias pós florada).",
            nivelAcaoEmbrapa = "3% a 5% de frutos brocados em frutos no estádio chumbão.",
            inimigosNaturais = "Vespa-de-uganda (Cephalonomia stephanoderis) e fungo Beauveria bassiana.",
            manejoRecomendado = "Colheita bem feita e repasse para não deixar frutos remanescentes nos ramos e no chão."
        )
    )

    val inimigosNaturais = listOf(
        InimigoNatural("Joaninhas (Coccinellidae)", "Predador", "Pulgões, ovos e ninfas de percevejo", "Voraz consumidora de sugadores; larvas e adultos se alimentam ativamente."),
        InimigoNatural("Crisopídeos / Bicho-lixeiro", "Predador", "Ovos de lagarta, pulgões e ácaros", "Larvas têm mandíbulas em foice que injetam suco digestivo."),
        InimigoNatural("Tesourinha (Doru luteipes)", "Predador", "Ovos e lagartas recém-eclodidas no cartucho", "Fundamental para controle natural da lagarta-do-cartucho do milho."),
        InimigoNatural("Trichogramma pretiosum", "Parasitoide", "Ovos de lagartas (Helicoverpa, Spodoptera)", "Micro-vespas que depositam ovos dentro dos ovos das pragas, impedindo a eclosão."),
        InimigoNatural("Telenomus remus / podisi", "Parasitoide", "Ovos de Spodoptera e percevejos", "Altíssima taxa de parasitismo de posturas em campo aberto."),
        InimigoNatural("Podisus nigrispinus", "Predador", "Lagartas desfolhadoras", "Percevejo predador benéfico que perfura e suga lagartas.")
    )

    val diagnosticos = listOf(
        DiagnosticoSintoma(
            problema = "Deficiência Nutricional",
            aparenciaVisual = "Clorose (amarelecimento) uniforme ou internerval, arroxeamento (fósforo) ou queima marginal (potássio).",
            distribuicaoNoCampo = "Segue gradiente de solo, manchas contínuas por tipo de terra ou relevo. Afeta folhas velhas (nutrientes móveis N, P, K, Mg) ou folhas novas (imóveis Ca, B, Fe).",
            acaoImediata = "Realizar análise foliar e análise química do solo de 0-20 cm e 20-40 cm para confirmar balanço NPK e micronutrientes."
        ),
        DiagnosticoSintoma(
            problema = "Doença Fúngica ou Bacteriana",
            aparenciaVisual = "Manchas com halo concêntrico, pústulas com esporulação na página inferior, murcha vascular, podridão úmida de colo ou raízes.",
            distribuicaoNoCampo = "Focos iniciais em reboleiras aleatórias onde o microclima é mais favorável (baixas umedecidas, proximidade de matas).",
            acaoImediata = "Isolar amostra fresca em saco de papel e encaminhar para diagnose laboratorial. Evitar aplicação de inseticida."
        ),
        DiagnosticoSintoma(
            problema = "Ataque de Praga / Inseto",
            aparenciaVisual = "Perfurações com bordas irregulares, raspagens, folhas dobradas/enroladas com teias, fezes visíveis, brocas em colmo ou vagens.",
            distribuicaoNoCampo = "Muitas vezes inicia em bordaduras, carreadores ou migrando de talhões colhidos vizinhos.",
            acaoImediata = "Realizar pano de batida ou vistoria visual quantitativa para verificar se atinge o Nível de Ação do MIP antes de intervir."
        ),
        DiagnosticoSintoma(
            problema = "Fitotoxidade / Deriva de Herbicida",
            aparenciaVisual = "Epinastia (retorcimento de ramos e folhas), amarelecimento apical súbito, folhas em leque ou manchas de queimadura em gotas.",
            distribuicaoNoCampo = "Formato de faixas retas acompanhando a passada do pulverizador, sobreposição nas pontas ou gradiente na direção do vento.",
            acaoImediata = "Verificar histórico de aplicação nas últimas 72h, direção do vento durante a pulverização vizinha e limpeza de tanque."
        ),
        DiagnosticoSintoma(
            problema = "Estresse Hídrico e Compactação",
            aparenciaVisual = "Murcha nas horas mais quentes do dia, enrolamento de folhas de milho (charuto), raízes tortas ou em formato de L (pés-de-arado).",
            distribuicaoNoCampo = "Reboleiras em áreas de tráfego pesado de máquinas, cabeceiras de talhão ou manchas com cascalho/solo arenoso raso.",
            acaoImediata = "Abrir trincheira de campo para avaliar profundidade efetiva das raízes, presença de camada compactada e teor de umidade do perfil."
        )
    )

    val checklistFases = listOf(
        ChecklistItem(
            fase = "1. Antes do Plantio (Planejamento)",
            acoes = listOf(
                "Conferir análise de solo (0-20 e 20-40 cm) e necessidade de calagem/gessagem.",
                "Mapear reboleiras históricas de plantas daninhas difíceis e nematoides.",
                "Realizar amostragem de pragas subterrâneas (corós, percevejo-castanho, larva-arame).",
                "Verificar laudo do lote de sementes (Pureza %, Germinação % e PMS em gramas).",
                "Calcular população de plantas, estande planejado e regulagem da semeadora.",
                "Revisar uniformidade de distribuição dos discos e dosadores da plantadeira.",
                "Planejar dessecação pré-plantio e rotação de mecanismos de ação."
            )
        ),
        ChecklistItem(
            fase = "2. Pós-Emergência (Estande Inicial)",
            acoes = listOf(
                "Contar plantas por metro em pelo menos 5 a 10 pontos do talhão.",
                "Calcular população real por hectare e comparar com a meta planejada.",
                "Mapear falhas, duplas, sementes não germinadas e profundidade de deposição.",
                "Inspecionar raízes, coleto e sintomas de tombamento (damping-off).",
                "Registrar primeiros focos de plantas daninhas no período crítico de interferência.",
                "Averiguar presença de pragas iniciais (lagarta-elasmo, corós, tripes)."
            )
        ),
        ChecklistItem(
            fase = "3. Desenvolvimento Vegetativo",
            acoes = listOf(
                "Realizar vistorias semanais MIP com pano de batida ou contagem visual.",
                "Acompanhar fechamento das entrelinhas e altura média do dossel.",
                "Registrar percentual de desfolha e contagem de lagartas e percevejos.",
                "Contabilizar e preservar inimigos naturais (joaninhas, tesourinhas, aranhas).",
                "Monitorar água disponível no solo e ajustar lâmina de irrigação se aplicável.",
                "Avaliar estado nutricional e sintomas de deficiência foliar."
            )
        ),
        ChecklistItem(
            fase = "4. Fase Reprodutiva (Crítica)",
            acoes = listOf(
                "Contar estruturas reprodutivas: botões, flores, espigas, vagens ou panículas.",
                "Vistoriar rigorosamente percevejos e brocas que atacam vagens e grãos.",
                "Avaliar abortamento de flores/vagens e qualidade do enchimento de grãos.",
                "Fazer estimativa pré-colheita com amostragem de plantas e contagem de grãos.",
                "Acompanhar previsão meteorológica e programar dessecação ou colheita."
            )
        ),
        ChecklistItem(
            fase = "5. Colheita e Pós-Colheita",
            acoes = listOf(
                "Medir perdas na colheita usando armação de 2 m² da Embrapa.",
                "Separar perda de plataforma e perda de mecanismos internos da máquina.",
                "Aferir umidade dos grãos na colheita e descontos de impureza.",
                "Calcular produtividade real líquida por talhão e arquivar no histórico.",
                "Limpar colheitadeira, armazém e moegas para evitar proliferação de pragas de grãos.",
                "Mapear manchas de fertilidade e produtividade para adubação da próxima safra."
            )
        )
    )

    val errosComuns = listOf(
        ErroComum(
            titulo = "Usar centímetros na fórmula sem converter para metros",
            impacto = "Provoca erro monumental de 100x a 10.000x na densidade ou no volume por hectare.",
            solucaoCorreta = "Se o espaçamento for 45 cm, divida por 100 e use estritamente 0,45 m na fórmula."
        ),
        ErroComum(
            titulo = "Confundir espaçamento entre linhas com espaçamento entre plantas",
            impacto = "Inverte as taxas de semeadura na regulagem das engrenagens da plantadeira.",
            solucaoCorreta = "Espaçamento entre linhas é a distância entre as linhas da máquina (ex: 0,50 m). Entre plantas é a distância na mesma linha (ex: 0,20 m)."
        ),
        ErroComum(
            titulo = "Usar população de sementes como se fosse o estande final de plantas",
            impacto = "Lavoura nasce rala, perde potencial de fechamento de entrelinhas e permite invasão de daninhas.",
            solucaoCorreta = "Sempre desconte Germinação (%) e Emergência de Campo (%) na fórmula de semeadura: Sementes/ha = Alvo / (G × E)."
        ),
        ErroComum(
            titulo = "Calcular produtividade com massa úmida sem corrigir a umidade",
            impacto = "Gera falsa expectativa de lucro: grãos a 18% pesam mais por conter água, mas sofrerão desconto na entrega.",
            solucaoCorreta = "Use a fórmula de correção de umidade: Massa Corrigida = Massa × (100 - Umidade Medida) / (100 - Umidade Padrão Comercial)."
        ),
        ErroComum(
            titulo = "Extrapolar uma única amostra de 1 m² para todo o talhão",
            impacto = "Erro amostral grave que distorce completamente a safra estimada da fazenda.",
            solucaoCorreta = "Colete pelo menos 5 a 10 pontos representativos em zigue-zague, separando áreas altas, médias e baixadas."
        ),
        ErroComum(
            titulo = "Contar apenas plantas na bordadura do talhão",
            impacto = "As bordas recebem mais luz, poeira e pragas, não refletindo o interior da lavoura.",
            solucaoCorreta = "Adentre pelo menos 20 a 50 metros para dentro do talhão antes de iniciar as coletas."
        ),
        ErroComum(
            titulo = "Aplicar inseticida apenas por ver insetos sem estimar o dano",
            impacto = "Gasto financeiro desnecessário, contaminação ambiental e extermínio dos inimigos naturais benéficos.",
            solucaoCorreta = "Monitore com pano de batida e aplique apenas quando atingir o Nível de Ação oficial (ex: 2 percevejos/pano na soja grão)."
        ),
        ErroComum(
            titulo = "Repetir o mesmo mecanismo de ação de defensivo continuamente",
            impacto = "Seleção rápida de biótipos de plantas daninhas ou pragas resistentes.",
            solucaoCorreta = "Alterne códigos HRAC/IRAC e combine táticas químicas com palhada, rotação e controle biológico."
        )
    )
}
