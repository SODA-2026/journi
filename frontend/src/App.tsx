import { fetchAllUsers } from './api/fetchData'
import './App.css'
import { useEffect, useState } from 'react'


function App() {
  const [users, setUsers] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await fetchAllUsers()
        setUsers(data)
      } catch (err) {
        setError('Kunde inte nå backend: ' + err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  if (loading) return <p>Laddar...</p>
  if (error) return <p>{error}</p>

  return (
    <ul>
      {users.map((user) => (
        <li key={user.name}>{user.name}</li>
      ))}
    </ul>
  )
}

export default App