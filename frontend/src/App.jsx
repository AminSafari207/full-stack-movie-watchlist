import MainContainer from 'src/pages/main-container/MainContainer'
import {
    Backdrop, Box,
    Button, CircularProgress,
    Container,
    Divider,
    OutlinedInput,
    Paper,
    Stack,
    styled,
    TextField,
    Typography
} from "@mui/material";
import {useEffect, useState} from "react";
import {addUser, getUsers} from "src/shared/api-services/mainApiService";

const PaperContainer = styled((props) => (
    <Paper elevation={12} {...props} />
))(() => ({
    display: "flex",
    flexDirection: "column",
    padding: 32,
    borderRadius: 8,
    gap: 16
}))

const DEFAULT_USER_FORM_DATA = {
    username: "",
    email: "",
    password: ""
}

export const App = () => {
  const [userBackdrop, setUserBackdrop] = useState(false);
  const [userFormButtonActive, setUserFormButtonActive] = useState(true);
  const [userFormData, setUserFormData] = useState(DEFAULT_USER_FORM_DATA);
  const [usersList, setUsersList] = useState([])

  const resetUserFormData = () => setUserFormData({ ...DEFAULT_USER_FORM_DATA })

  const handleOpenUserBackdrop = () => setUserBackdrop(true)
  const handleCloseUserBackdrop = () => {
      resetUserFormData()
      setUserBackdrop(false)
  }
  const activateUserFormButton = () => setUserFormButtonActive(true)
  const deactivateUserFormButton = () => setUserFormButtonActive(false)
  const handleUserFormChange = (e) => {
      const { name, value } = e.target;
      setUserFormData((prev) => ({ ...prev, [name]: value }));
  }

  const addUserToList = (newUser) => {
      setUsersList(prev => {
          const updated = [...prev, newUser]
          return updated.sort((a, b) => a.id - b.id)
      })
  }

  const handleSubmitUserForm = async (e) => {
      e.preventDefault()
      deactivateUserFormButton()

      try {
        const userResp = await addUser(userFormData)

        if (userResp.status >= 200 && userResp.status < 300) {
            addUserToList(userResp.data)
            resetUserFormData()
            activateUserFormButton()
            handleCloseUserBackdrop()
        }
      } catch (err) {
          activateUserFormButton()
          console.error("Error adding user: ", err)
      }
  }

    useEffect(() => {
        getUsers().then(res => setUsersList(res?.data || []))
    }, []);

  return (
    <div data-component="App">
      <MainContainer>
          <PaperContainer sx={{ width: 300 }}>
            <Stack direction="row" justifyContent="space-between">
                <Typography variant="h6" fontWeight={300}>Users List</Typography>
                <Button variant="outlined" onClick={handleOpenUserBackdrop} sx={{ maxWidth: 120 }}>Add User</Button>
            </Stack>

            <Divider sx={{ my: 2 }}/>

            <Box sx={{ maxHeight: 300, mx: "auto", overflowY: "auto"}}>
                {
                    !usersList.length ?
                        <CircularProgress /> :
                        usersList.map(user => (
                            <div key={user?.id}>
                                <Typography>ID: {user?.id}</Typography>
                                <Typography>Username: {user?.username}</Typography>
                                <Typography>Email: {user?.email}</Typography>
                                <Divider variant="middle" sx={{ my: 2 }} />
                            </div>
                        ))
                }
            </Box>

            <Backdrop open={userBackdrop}>
                <form onSubmit={handleSubmitUserForm}>
                    <PaperContainer>
                        <Typography variant="h6" align="center">Add New User</Typography>

                        <Divider variant="middle" sx={{ my: 1 }} />

                        <TextField
                            name="username"
                            label="Enter Username"
                            type="text"
                            required
                            value={userFormData.username}
                            onChange={handleUserFormChange}
                        />
                        <TextField
                            name="email"
                            label="Enter Email"
                            type="email"
                            required
                            value={userFormData.email}
                            onChange={handleUserFormChange}
                        />
                        <TextField
                            name="password"
                            label="Enter Password"
                            type="password"
                            required
                            value={userFormData.password}
                            onChange={handleUserFormChange}
                        />

                        <Stack direction="row" spacing={1} justifyContent="flex-end" mt={2}>
                            <Button
                                variant="contained"
                                color="inherit"
                                onClick={handleCloseUserBackdrop}
                                disabled={!userFormButtonActive}
                            >
                                Cancel
                            </Button>
                            <Button
                                variant="contained"
                                type="submit"
                                disabled={!userFormButtonActive}
                                loading={!userFormButtonActive}
                            >
                                Submit
                            </Button>
                        </Stack>
                    </PaperContainer>
                </form>
            </Backdrop>
        </PaperContainer>
      </MainContainer>
    </div>
  )
}
