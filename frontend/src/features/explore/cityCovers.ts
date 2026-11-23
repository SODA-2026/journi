// Temporary stand-in for real city photos: an inline SVG per city so the cards
// look finished without a DB column, image files or network requests.
// Replace with `city.imageUrl` once the team decides on real images.

import type { City } from "../destinations/destination";

function hash(value: string): number {
    let result = 0;
    for (let i = 0; i < value.length; i++) {
        result = (result << 5) - result + value.charCodeAt(i);
        result |= 0;
    }
    return Math.abs(result);
}

function escapeXml(value: string): string {
    const entities: Record<string, string> = {
        "<": "&lt;",
        ">": "&gt;",
        "&": "&amp;",
        '"': "&quot;",
        "'": "&apos;",
    };
    return value.replace(/[<>&"']/g, (char) => entities[char]);
}

export function cityCover(city: City, countryName?: string): string {
    const hue = hash(city.name) % 360;
    const countryLine = countryName
        ? `<text x="320" y="326" text-anchor="middle" font-family="system-ui, sans-serif" font-size="20" fill="#ffffff" opacity="0.8">${escapeXml(countryName)}</text>`
        : "";

    const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="640" height="360" viewBox="0 0 640 360">
<defs><linearGradient id="bg" x1="0" y1="0" x2="1" y2="1">
<stop offset="0" stop-color="hsl(${hue}, 46%, 38%)"/>
<stop offset="1" stop-color="hsl(${(hue + 35) % 360}, 52%, 20%)"/>
</linearGradient></defs>
<rect width="640" height="360" fill="url(#bg)"/>
<circle cx="540" cy="60" r="90" fill="#ffffff" opacity="0.06"/>
<circle cx="80" cy="330" r="120" fill="#ffffff" opacity="0.05"/>
<path d="M320 96a44 44 0 0 0-44 44c0 32 44 74 44 74s44-42 44-74a44 44 0 0 0-44-44z" fill="#ffffff" opacity="0.92"/>
<circle cx="320" cy="140" r="16" fill="hsl(${hue}, 46%, 32%)"/>
<text x="320" y="288" text-anchor="middle" font-family="system-ui, sans-serif" font-size="36" font-weight="700" fill="#ffffff">${escapeXml(city.name)}</text>
${countryLine}
</svg>`;

    return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`;
}
