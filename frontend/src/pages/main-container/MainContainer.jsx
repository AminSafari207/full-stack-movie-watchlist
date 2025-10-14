import { Box, Button, Grid, styled, TextField, Typography } from '@mui/material'

const Container = styled(Box)(() => ({
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center',
  minHeight: '100vh',
  gap: 16,
  backgroundColor: '#D3DAD9',
}))

export default function MainContainer({children}) {
  return (
    <Container>
        {children}
    </Container>
  )
}
