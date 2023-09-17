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
import { Brightness4, Brightness7 } from '@material-ui/icons';

const theme = createMuiTheme({
    palette: {
        type: 'light',
    },
});

const useStyles = makeStyles((theme) => ({
    darkModeToggle: {
        position: 'absolute',
        top: 10,
        right: 10,
    },
}));

function App() {
    const [responses, setResponses] = useState({});
    const [darkMode, setDarkMode] = useState(false);

    const classes = useStyles();

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
                setResponses(prevResponses => ({
                    ...prevResponses,
                    [endpoint.uniqueId]: {
                        ...prevResponses[endpoint.uniqueId],
                        response: response.data,
                        status: response.status
                    }
                }));
            })
            .catch(error => {
                console.error('Error sending ad-hoc request:', error);
            });
    }

    const fetchEndpoints = () => {
        axios.get('http://localhost:8080/endpoints')
            .then(response => {
                setResponses(response.data);
            })
            .catch(error => {
                console.error('Error fetching endpoints:', error);
            });
    }

    useEffect(() => {
        fetchEndpoints();
    }, []);

    const toggleDarkMode = () => {
        setDarkMode(!darkMode);
    };

    return (
        <ThemeProvider theme={darkMode ? createMuiTheme({ palette: { type: 'dark' } }) : theme}>
            <CssBaseline />
            <Container>
                <Tooltip title={darkMode ? 'Light Mode' : 'Dark Mode'}>
                    <Fab color="primary" className={classes.darkModeToggle} onClick={toggleDarkMode}>
                        {darkMode ? <Brightness7 /> : <Brightness4 />}
                    </Fab>
                </Tooltip>
                <Typography variant="h3" gutterBottom>Endpoint Responses</Typography>
                <List>
                    {Object.keys(responses).map(endpointKey => {
                        const endpoint = responses[endpointKey];
                        const statusColor = endpoint.status === 200 ? 'green' : 'red';
                        return (
                            <ListItem key={endpointKey}>
                                <ListItemText primary={
                                    <span style={{ color: statusColor }}>
                                        Unique ID: {endpoint.uniqueId}
                                    </span>
                                } />
                                <Button variant="contained" color="primary" onClick={() => sendAdHocRequest(endpoint)}>
                                    Send Ad-Hoc Request
                                </Button>
                                <div>
                                    <Typography variant="body1">Response:</Typography>
                                    <pre>{JSON.stringify(endpoint.response, null, 2)}</pre>
                                </div>
                            </ListItem>
                        );
                    })}
                </List>
            </Container>
        </ThemeProvider>
    );
}

export default App;