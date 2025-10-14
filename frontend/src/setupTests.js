import '@testing-library/jest-dom'
import dotenv from 'dotenv'

const env = dotenv.config().parsed

global.API_BASE_URL = process.env.USE_MOCK_API === 'true' ? env.EXPRESS_MOCK_SERVER : 'https://real-api.example.com/api'
