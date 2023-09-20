import React, {useState, useEffect} from 'react';
import axios from 'axios';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import {Accordion, AccordionSummary, AccordionDetails} from '@material-ui/core';

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
import {Brightness4, Brightness7} from '@material-ui/icons';

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
const getStableStatus = (failPercent) => {
    if (failPercent < 50) return 'Unstable';
    if (failPercent >= 50 && failPercent <= 75) return 'SO-SO';
    return 'Stable';
}

const getStableColor = (failPercent) => {
    if (failPercent < 50) return 'red';
    if (failPercent >= 50 && failPercent <= 75) return 'orange'; // Amber color
    return 'green';
}

function App() {
    const [responses, setResponses] = useState({});
    const [darkMode, setDarkMode] = useState(false);
    const classes = useStyles();
    const [longPollingResponse, setLongPollingResponse] = useState(null);
    const [testResults, setTestResults] = useState([]);
    const sendAdHocRequest = (endpoint) => {
        const requestObject = {
            uniqueId: endpoint.uniqueId,
            url: endpoint.url,
            method: endpoint.method,
            endpoint: endpoint.endpoint,
            requestBody: endpoint.requestBody,
            contentType: endpoint.contentType
        };

        // Assuming responses is an array of objects received from the server

        const groupedResponses = responses.reduce((acc, response) => {
            const uniqueIdParts = response.uniqueId.split('-'); // Assuming uniqueId is in the format "application-feature-env-stripe-cluster"

            const key = `${uniqueIdParts[0]}-${uniqueIdParts[2]}-${uniqueIdParts[3]}`; // Grouping by application, env, and stripe

            if (!acc[key]) {
                acc[key] = [];
            }

            acc[key].push(response);
            return acc;
        }, {});

// Now groupedResponses is an object where each key is a group (e.g., "marketing-qa-blue") and the value is an array of responses in that group.

// You can now render the grouped responses in your component.



        axios.post('http://localhost:8080/sendAdHocRequest', requestObject, {headers: {'Content-Type': 'application/json'}})
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

    const startLongPolling = async () => {
        try {
            const response = await axios.get('http://localhost:8080/long-polling');
            setLongPollingResponse(response.data);
        } catch (error) {
            console.error('Error during long polling:', error);
        } finally {
            startLongPolling(); // Start a new long-polling request after completion
        }
    }

    useEffect(() => {
        startLongPolling();
    }, []);


    const fetchTestResults = () => {
        axios.get('http://localhost:8080/getTestResults')
            .then(response => {
                setTestResults(response.data);
            })
            .catch(error => {
                console.error('Error fetching test results:', error);
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
        // Fetch endpoints initially
        fetchEndpoints();

        // Set up an interval to fetch endpoints every 5 seconds (adjust as needed)
        const interval = setInterval(fetchEndpoints, 5000);

        // Clean up the interval when the component is unmounted
        return () => clearInterval(interval);
    }, []);


    return (
        <ThemeProvider theme={darkMode ? createMuiTheme({palette: {type: 'dark'}}) : theme}>
            <CssBaseline/>
            <Container>
                <Tooltip title={darkMode ? 'Light Mode' : 'Dark Mode'}>
                    <Fab color="primary" className={classes.darkModeToggle} onClick={toggleDarkMode } style={{fontSize: '1rem'}}>
                        {darkMode ? <Brightness7/> : <Brightness4/>}
                    </Fab>
                </Tooltip>
                <Typography variant="h3" gutterBottom>Endpoint Responses</Typography>
                <List>
                    {Object.values(responses).map(endpoint => {
                        const statusColor = endpoint.status === 200 ? 'green' : 'red';
                        return (
                            <Card key={endpoint.uniqueId} style={{ marginBottom: '1rem' }}>
                                <CardContent>
                                    <Typography variant="h5" component="div" style={{ color: statusColor }}>
                                        Application: {endpoint.application}<br/>
                                        Environment: {endpoint.environment}<br/>
                                        Stripe: {endpoint.stripe}<br/>
                                        Cluster: {endpoint.cluster}<br/>
                                        Unique ID: {endpoint.uniqueId}
                                    </Typography>
                                    <Button variant="contained" color="primary" onClick={() => sendAdHocRequest(endpoint)}>
                                        Send Ad-Hoc Request
                                    </Button>
                                    <div>
                                        <Typography variant="body1">Response:</Typography>
                                        <pre>{JSON.stringify(endpoint.response, null, 2)}</pre>
                                    </div>
                                </CardContent>
                            </Card>
                        );
                    })}
                </List>
            </Container>

            <Container>
                <Container>
                    <Accordion>
                        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                            <Typography variant="h5" gutterBottom>Test Results</Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                            <List>
                                {testResults.map((result, index) => (
                                    <ListItem key={index}>
                                        <ListItemText primary={`Application: ${result.application}`} />
                                        <ListItemText primary={`Environment: ${result.env}`} />
                                        <ListItemText primary={`Tag: ${result.tag}`} />
                                        <ListItemText primary={`Pass Percent: ${result.passPercent}`} />
                                        <ListItemText primary={`Fail Percent: ${result.failPercent}`} />
                                        <ListItemText primary={`Date and Time: ${result.dateTime}`} />
                                    </ListItem>
                                ))}
                            </List>
                        </AccordionDetails>
                    </Accordion>

                </Container>
            </Container>
        </ThemeProvider>
    );
}

export default App;