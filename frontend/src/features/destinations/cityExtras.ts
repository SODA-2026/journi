export type Season = "Vår" | "Sommar" | "Höst" | "Vinter";

export type CityExtra = {
    bestSeasons: Season[];
    images: CityImage[];
};

export type CityImage = {
    url: string;
    author: string;
    license: string;
    licenseUrl: string;
};

const commons = (fileName: string) =>
    `https://commons.wikimedia.org/wiki/Special:FilePath/${encodeURIComponent(
        fileName
    )}?width=1280`;

export const cityExtras: Record<string, CityExtra> = {
    Stockholm: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons(
                    "Stockholm_August_2020_-_Kastellet,_Vasa_Museum,_and_Nordic_Museum.jpg"
                ),
                author: "Martin Falbisoner",
                license: "CC BY-SA 4.0",
                licenseUrl:
                    "https://creativecommons.org/licenses/by-sa/4.0/deed.en",
            },
        ],
    },
    Kiruna: {
        bestSeasons: ["Vinter"],
        images: [
            {
                url: commons("Kiruna_kyrka_September_2017_04.jpg"),
                author: "Arild Vågen",
                license: "CC BY-SA 4.0",
                licenseUrl:
                    "https://creativecommons.org/licenses/by-sa/4.0/deed.en",
            },
        ],
    },
    Ålborg: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Aalborg_2010_-_125_ubt.JPG"),
                author: "Tomasz Sienicki",
                license: "CC BY-SA 3.0",
                licenseUrl:
                    "https://creativecommons.org/licenses/by-sa/3.0/deed.en",
            },
        ],
    },
    Ålesund: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons(
                    "Vista_de_Ålesund_desde_Aksla,_Noruega,_2019-09-01,_DD_16.jpg"
                ),
                author: "Diego Delso, delso.photo",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Alta: {
        bestSeasons: ["Sommar", "Vinter"],
        images: [
            {
                url: commons("Alta_Church_from_Air_-_Oct_2018.jpg"),
                author: "Lars Palczak",
                license: "CC BY-SA 4.0",
                licenseUrl:
                    "https://creativecommons.org/licenses/by-sa/4.0/deed.en",
            },
        ],
    },

    Århus: {
        bestSeasons: ["Sommar", "Höst"],
        images: [
            {
                url: commons(
                    "Urban_community_gardens_Aarhus_City_Park_Pier_2_Aarhus_Denmark_3.jpg"
                ),
                author: "Ciara Ní Riain",
                license: "CC BY-SA 4.0",
                licenseUrl:
                    "https://creativecommons.org/licenses/by-sa/4.0/deed.en",
            },
        ],
    },

    Bergen: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons(
                    "Vista_de_Bergen_desde_la_montaña_Fløyen,_Noruega,_2019-09-08,_DD_27-31_PAN.jpg"
                ),
                author: "Diego Delso, delso.photo",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Billund: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Legoland_Billund_-_Trafikskolen_04.jpg"),
                author: "Leif Jørgensen",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
            {
                url: commons("Legoland_Billund,_1977_(02).jpg"),
                author: "Bahnfrend",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Bodö: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Bodø_marina_6.jpg"),
                author: "kallerna",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Esbjerg: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Den_hemmelige_skov,_Esbjerg,_7.jpg"),
                author: "Kent Madsen",
                license: "CC BY-SA 2.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/2.0/",
            },
            {
                url: commons("Esvagt,_Esbjerg_harbour.jpg"),
                author: "Kent Madsen",
                license: "CC BY 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by/4.0/",
            },
        ],
    },

    Göteborg: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Maritiman_September_2019_03.jpg"),
                author: "Arild Vågen",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Helsingborg: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Inre_hamnen_i_Helsingborg.jpg"),
                author: "David Castor",
                license: "Public domain",
                licenseUrl:
                    "https://commons.wikimedia.org/wiki/File:Inre_hamnen_i_Helsingborg.jpg",
            },
        ],
    },

    Helsingör: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Helsingör_Station_2025.jpg"),
                author: "Thomas Dahlstrøm Nielsen",
                license: "CC BY 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by/4.0/",
            },
        ],
    },

    Köpenhamn: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Christiansborg_Slot_Copenhagen_2014_01.jpg"),
                author: "Julian Herzog",
                license: "CC BY 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by/4.0/",
            },
            {
                url: commons("2018_-_Nyhavn_on_sunset.jpg"),
                author: "Moahim",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Kristiansand: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons(
                    "Kristiansand_Fähre_Kristiansand_-_Hirtshals_lub_2025-07-31_img07.jpg"
                ),
                author: "Lukas Beck",
                license: "CC BY 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by/4.0/",
            },
        ],
    },

    Lillehammer: {
        bestSeasons: ["Vinter", "Sommar"],
        images: [
            {
                url: commons(
                    '"Maihaugen_ist_eines_der_größten_Museen_Norwegens"._(Auf_dem_Hof_des_Bauern_Anders)_04.jpg'
                ),
                author: "Holger Uwe Schmitt",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
            {
                url: commons(
                    "Panorama_of_Lysgårdbakkene_Ski_Jumping_Arena_(21649973833).jpg"
                ),
                author: "Lillehammer 2016 Youth Olympic Games",
                license: "CC BY 2.0",
                licenseUrl: "https://creativecommons.org/licenses/by/2.0/",
            },
        ],
    },

    Lund: {
        bestSeasons: ["Sommar", "Vår"],
        images: [
            {
                url: commons("Lund,_case_a_graticcio_su_petriplatsen.jpg"),
                author: "Francesco Bini",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Malmö: {
        bestSeasons: ["Sommar", "Vår"],
        images: [
            {
                url: commons(
                    "Turning_Torso_in_Malmö,_Sweden,_near_Öresund.jpg"
                ),
                author: "OleNeitzel",
                license: "CC BY 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by/4.0/",
            },
        ],
    },

    Odense: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Odense_Rathaus_und_Dom.JPG"),
                author: "Okänd",
                license: "Public domain",
                licenseUrl:
                    "https://commons.wikimedia.org/wiki/File:Odense_Rathaus_und_Dom.JPG",
            },
        ],
    },

    Örebro: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Örebro_slott_May_2014_01.jpg"),
                author: "Arild Vågen",
                license: "CC BY-SA 3.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/3.0/",
            },
        ],
    },

    Oslo: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Oslo_Opera_House_-_2025.jpg"),
                author: "Pierre Blaché",
                license: "CC0 1.0",
                licenseUrl:
                    "https://creativecommons.org/publicdomain/zero/1.0/",
            },
        ],
    },

    Ribe: {
        bestSeasons: ["Sommar", "Höst"],
        images: [
            {
                url: commons("Johanne_Dan,_Ribe_Denmark_(2026-08-10)_1.jpg"),
                author: "Olgierd Rudak",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
            {
                url: commons("Ribe_Domkirke_Denmark_(2026-08-10).jpg"),
                author: "Olgierd Rudak",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Roskilde: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Roskilde_Cathedral_aerial.jpg"),
                author: "CucombreLibre",
                license: "CC BY 2.0",
                licenseUrl: "https://creativecommons.org/licenses/by/2.0/",
            },
        ],
    },

    Skagen: {
        bestSeasons: ["Sommar", "Höst"],
        images: [
            {
                url: commons("Torvepladsen,_Skagen_2021_01.jpg"),
                author: "Schorle",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Stavanger: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons(
                    "Stavanger_Vågen_Skagenkaien_Norway_(2022.06.23).jpg"
                ),
                author: "Geir Hval (www.MacWhale.eu)",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Tromsö: {
        bestSeasons: ["Vinter", "Sommar"],
        images: [
            {
                url: commons("Tromso_Tromsø_Norway_tunliweb_02.jpg"),
                author: "Svein-Magne Tunli - tunliweb.no",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Trondheim: {
        bestSeasons: ["Sommar", "Vinter"],
        images: [
            {
                url: commons(
                    "Houses_on_Nidelva_Riverfront,_Trondheim,_West_view_20150605_2.jpg"
                ),
                author: "DXR",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Umeå: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Umea_banner_Ume_alv.jpg"),
                author: "Olsin.se",
                license: "CC BY-SA 3.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/3.0/",
            },
        ],
    },

    Uppsala: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Uppsala_domkyrka,_flygbild.jpg"),
                author: "Kateryna Baiduzha",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },

    Visby: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons("Visby_ringmur_June_2026_01.jpg"),
                author: "Arild Vågen",
                license: "CC BY-SA 4.0",
                licenseUrl: "https://creativecommons.org/licenses/by-sa/4.0/",
            },
        ],
    },
};
