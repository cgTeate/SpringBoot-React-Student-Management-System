import Head from 'next/head'
import Image from 'next/image'
import { Inter } from 'next/font/google'
import { getAllStudents } from './api/client'
import {Button, Radio} from 'antd'

const inter = Inter({ subsets: ['latin'] })

export default function Home() {
  return (
    <>
      <div className="App">
          <Button type="primary">Hello</Button>
          <Radio.Group value='large'>
            <Radio.Button value="large">Large</Radio.Button>
            <Radio.Button value="default">Default</Radio.Button>
            <Radio.Button value="small">Small</Radio.Button>
          </Radio.Group>
      </div>
    </>
    
  )
}
