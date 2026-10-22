import './App.css'

import { useEffect, useState } from 'react'

function App() {
  const [message, setMessage] = useState('Laddar...')

  useEffect(() => {
    fetch('/api/test')
      .then((res) => res.json())
      .then((data) => setMessage(data.message))
      .catch(() => setMessage('Kunde inte nå backend'))
  }, [])

  return <h1>{message}</h1>
}

export default App
