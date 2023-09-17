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
import { createMuiTheme } from '@material-ui/core/styles';

const theme = createMuiTheme({
    palette: {
        type: 'light',
    },
});

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
    const [responses, setResponses] = useState([]);

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
                // Update the response in state
                setResponses(prevResponses => ({
                    ...prevResponses,
                    [endpoint.uniqueId]: {
                        ...prevResponses[endpoint.uniqueId],
                        response: response.data
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

    const classes = useStyles();

    return (
        <ThemeProvider theme={darkMode ? createTheme({
            palette: {
                type: 'dark',
            },
        }) : theme}>
            <CssBaseline />
            <Container>
                <Typography variant="h3" align="center" gutterBottom>Environment Stability View</Typography>
                <List>
                    {Object.keys(responses).map(endpointKey => (
                        <ListItem key={endpointKey}>
                            <ListItemText primary={`Unique ID: ${responses[endpointKey].uniqueId}`} />
                            <Button variant="contained" color="primary" onClick={() => sendAdHocRequest(responses[endpointKey])}>
                                Send Ad-Hoc Request
                            </Button>
                            <div>
                                <Typography variant="body1">Response:</Typography>
                                <pre>{JSON.stringify(responses[endpointKey].response, null, 2)}</pre>
                            </div>
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
