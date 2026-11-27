import trolltungan from "../images/backgrounds/trolltungan.png"
import aiView from "../images/backgrounds/Gemini_Generated_Image_fvs7psfvs7psfvs7.jpg"
import paperBackground from "../images/backgrounds/pappersark.png"
import scenery1 from "../images/backgrounds/scenery1.jpg"
import scenery2 from "../images/backgrounds/scenery2.jpg"
import scenery3 from "../images/backgrounds/scenery3.jpg"
import scenery4 from "../images/backgrounds/scenery4.jpg"

export function HomePage() {


    return (
        <div className="h-screen overflow-hidden relative">
            <section 
                className="h-[60%] bg-cover bg-center"
                style={{backgroundImage: `url(${scenery3})`}}
                >
                <div className="flex flex-col items-center">
                    <h1 className="mt-30 text-6xl font-bold text-amber-100 drop-shadow-[0_1.2px_1.2px_rgba(0,0,0,0.8)]">Planera ditt nästa äventyr</h1>
                </div>
            </section>
            <section
                className="absolute bottom-0 left-0 right-0 h-[50%] z-10 bg-top bg-no-repeat"
                style={{
                    backgroundImage: `url(${paperBackground})`,
                    backgroundSize: "100% 100%"
                }}
                >   
            </section>
        </div>
    );
}
