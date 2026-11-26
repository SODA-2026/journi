export type Season = "Vår" | "Sommar" | "Höst" | "Vinter";

export type CityImage = {
    url: string;
    credit: string;
};

export type CityExtra = {
    bestSeasons: Season[];
    images: CityImage[];
};

const commons = (fileName: string) =>
    `https://commons.wikimedia.org/wiki/Special:FilePath/${fileName}?width=1280`;

export const cityExtras: Record<string, CityExtra> = {
    Stockholm: {
        bestSeasons: ["Sommar"],
        images: [
            {
                url: commons(
                    "Stockholm_August_2020_-_Kastellet,_Vasa_Museum,_and_Nordic_Museum.jpg"
                ),
                credit: "Foto: Martin Falbisoner, LICENS, via Wikimedia Commons",
            },
            {
                url: commons("Rådhuset_metro_station_in_August_2019.jpg"),
                credit: "Foto: Frank Schulenburg, LICENS, via Wikimedia Commons",
            },
            {
                url: commons("Globen_September_2014_01.jpg"),
                credit: "Foto: Arild Vågen, LICENS, via Wikimedia Commons",
            },
        ],
    },
    Kiruna: {
        bestSeasons: ["Vinter"],
        images: [
            {
                url: commons("Kiruna_kyrka_September_2017_04.jpg"),
                credit: "Foto: Arild Vågen, LICENS, via Wikimedia Commons",
            },
            {
                url: commons("Kiruna_September_2017_05.jpg"),
                credit: "Foto: Arild Vågen, LICENS, via Wikimedia Commons",
            },
        ],
    },
};
