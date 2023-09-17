import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
    Button,
    List,
    ListItem,
    ListItemText,
    Typography,
    Container,
    CssBaseline,
    ThemeProvider,
    createTheme,
    makeStyles,
    Switch
} from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
    darkModeToggle: {
        position: 'absolute',
        top: 10,
        right: 10
    }
}));

function App() {
    const [endpoints, setEndpoints] = useState([]);
    const [darkMode, setDarkMode] = useState(false);

    const sendAdHocRequest = (endpoint) => {
        const requestObject = {
            uniqueId: endpoint.uniqueId,
            url: endpoint.url,
            method: endpoint.method,
            endpoint: endpoint.endpoint,
            requestBody: endpoint.requestBody,
            contentType: endpoint.contentType
        };

        axios.post('http://localhost:8080/sendAdHocRequest', requestObject, { headers: { 'Content-Type': 'application/json' } })
            .then(response => {
                console.log('Ad-hoc request sent successfully:', response);
            })
            .catch(error => {
                console.error('Error sending ad-hoc request:', error);
            });
    }

    const fetchEndpoints = () => {
        axios.get('http://localhost:8080/endpoints')
            .then(response => {
                setEndpoints(response.data);
            })
            .catch(error => {
                console.error('Error fetching endpoints:', error);
            });
    }

    useEffect(() => {
        fetchEndpoints();
    }, []);

    const theme = createTheme({
        palette: {
            type: darkMode ? 'dark' : 'light',
        },
    });

    const classes = useStyles();

    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <Container>
                <Typography variant="h3" align="center" gutterBottom>Endpoint Responses</Typography>
                <List>
                    {endpoints.map(endpoint => (
                        <ListItem key={endpoint.uniqueId}>
                            <ListItemText primary={`Unique ID: ${endpoint.uniqueId}`} />
                            <Button variant="contained" color="primary" onClick={() => sendAdHocRequest(endpoint)}>
                                Send Ad-Hoc Request
                            </Button>
                        </ListItem>
                    ))}
                </List>
                <Switch
                    className={classes.darkModeToggle}
                    checked={darkMode}
                    onChange={() => setDarkMode(!darkMode)}
                    color="primary"
                />
            </Container>
        </ThemeProvider>
    );
}

export default App;
