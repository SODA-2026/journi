import { useEffect, useState } from "react";
import { useParams } from "react-router";
import { getCityById } from "../ExploreApi";
import type { City } from "../../destinations/destination";
import { cityExtras } from "../../destinations/cityExtras";
import { Link } from "react-router";
import { cityCover } from "../cityCovers";

type CityInfoProps = {
    countryNames: Record<number, string>;
};

export function CityInfo({ countryNames }: CityInfoProps) {
    const [city, setCity] = useState<City>();
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    const params = useParams();
    const cityId = Number(params.id);

    useEffect(() => {
        let cancelled = false;
        getCityById(cityId)
            .then((data) => {
                if (!cancelled) setCity(data);
            })
            .catch((err) => {
                console.error(err);
                if (!cancelled) setError("Kunde inte hämta staden");
            })
            .finally(() => {
                if (!cancelled) setIsLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [cityId]);

    if (isLoading) {
        return <p className="text-lg text-[#1d4237]/70">Laddar städer...</p>;
    }

    if (error) {
        return (
            <div className="rounded-xl border border-red-200 bg-red-50 px-6 py-5">
                <p className="text-red-700">{error}</p>
            </div>
        );
    }

    if (!city) {
        return (
            <div className="rounded-xl border border-red-200 bg-red-50 px-6 py-5">
                <p className="text-red-700">staden hittades inte</p>
            </div>
        );
    }
    const countryName = countryNames[city.countryId];
    const cityExtra = cityExtras[city.name];
    const seasons = cityExtra?.bestSeasons ?? [];
    const images = cityExtra?.images ?? [];
    return (
        <div>
            <Link to={"/explore"}>Tillbaka</Link>
            <h1>{countryName}</h1>

            <p>{city.name}</p>
            <p>{city.shortDescription}</p>

            <section>
                <h2>Bästa restid</h2>
                {seasons.length > 0 ? (
                    <ul>
                        {seasons.map((season) => (
                            <li key={season}>{season}</li>
                        ))}
                    </ul>
                ) : (
                    <p>Ingen information än</p>
                )}
            </section>

            <section>
                <h2>Bilder</h2>
                {images.length > 0 ? (
                    <ul>
                        {images.map((image) => (
                            <li key={image.url}>
                                <img
                                    src={image.url}
                                    alt={`Bild på ${city.name}`}
                                />
                                <p>
                                    Foto: {image.author},{" "}
                                    <a
                                        href={image.licenseUrl}
                                        target="_blank"
                                        rel="noreferrer"
                                    >
                                        {image.license}
                                    </a>
                                </p>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <img src={cityCover(city, countryName)} alt={city.name} />
                )}
            </section>
        </div>
    );
}
