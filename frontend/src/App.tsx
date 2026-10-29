import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import './App.css'

type Trip = {
  id: number
  country: string
  city: string
  startDate: string
  endDate: string
}

function App() {
  const [demo, setDemo] = useState(false)
  useEffect(() => {
    const controller = new AbortController()
    fetch('/api/local-demo', { signal: controller.signal })
      .then(response => response.ok ? response.json() : null)
      .then(status => setDemo(status?.enabled === true))
      .catch(() => {})
    return () => controller.abort()
  }, [])
  const [country, setCountry] = useState('')
  const [city, setCity] = useState('')
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [pending, setPending] = useState(false)
  const [error, setError] = useState('')
  const [trip, setTrip] = useState<Trip | null>(null)

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (pending) return
    setError('')
    if (!country.trim() || !city.trim()) {
      setError('Ange både land och stad.')
      return
    }
    if (endDate < startDate) {
      setError('Slutdatum får inte vara före startdatum.')
      return
    }
    setPending(true)
    setTrip(null)
    try {
      const response = await fetch('/api/trips', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ country, city, startDate, endDate }),
      })
      if (!response.ok) {
        const problem = await response.json().catch(() => null)
        setError(problem?.detail ?? 'Resan kunde inte sparas. Försök igen.')
        return
      }
      setTrip(await response.json())
    } catch {
      setError('Kunde inte nå servern. Kontrollera att backend är igång.')
    } finally {
      setPending(false)
    }
  }

  return (
    <main className="trip-page">
      <p className="brand">Journi</p>
      {demo && <aside className="demo-banner" role="note">
        <strong>Demo: inloggning är avstängd.</strong>
        <p>Alla resor sparas i databasen med samma testanvändare. Använd detta läge bara lokalt.</p>
      </aside>}
      <h1>Vart går nästa resa?</h1>
      <p>Välj resmål och datum för att skapa din resa.</p>
      <form onSubmit={submit}>
        <fieldset disabled={pending}>
          <legend>Din resa</legend>
          <label htmlFor="country">Land</label>
          <input id="country" value={country} maxLength={100} required
            placeholder="Till exempel Italien"
            onChange={event => setCountry(event.target.value)} />
          <label htmlFor="city">Stad</label>
          <input id="city" value={city} maxLength={150} required
            placeholder="Till exempel Rom"
            onChange={event => setCity(event.target.value)} />
          <label htmlFor="start-date">Startdatum</label>
          <input id="start-date" type="date" value={startDate} required
            onChange={event => setStartDate(event.target.value)} />
          <label htmlFor="end-date">Slutdatum</label>
          <input id="end-date" type="date" value={endDate} min={startDate || undefined} required
            onChange={event => setEndDate(event.target.value)} />
          <button type="submit">{pending ? 'Sparar…' : 'Skapa resa'}</button>
        </fieldset>
        {error && <p role="alert" className="error">{error}</p>}
      </form>
      {trip && <section role="status" className="success">
        <h2>Resan är skapad!</h2>
        <p>{trip.city}, {trip.country}</p>
        <p>{trip.startDate} – {trip.endDate}</p>
        <p>Resans nummer: {trip.id}</p>
      </section>}
    </main>
  )
}

export default App
