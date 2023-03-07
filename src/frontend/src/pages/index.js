import Head from 'next/head'
import Image from 'next/image'
import { Inter } from 'next/font/google'
import { getAllStudents } from './api/client'
import { useEffect, useState } from 'react'

const inter = Inter({ subsets: ['latin'] })

export default function Home() {

  const [students, setStudents] = useState([]);

  const fetchStudents = () => {
    getAllStudents()
    .then(res => setStudents(res.data))
}

useEffect(()=>{
  console.log("component is mounted");
  fetchStudents();
 }, []);

  return (
    <>
      <h1>{students.length}</h1>
    </>
    
  )
}
