import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Button, List, ListItem, ListItemText, Typography, Container } from '@material-ui/core';

function App() {
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

    return (
        <Container>
            <Typography variant="h3" gutterBottom>Endpoint Responses</Typography>
            <List>
                {responses.map(endpoint => (
                    <ListItem key={endpoint.uniqueId}>
                        <ListItemText primary={`Unique ID: ${endpoint.uniqueId}`} />
                        <Button variant="contained" color="primary" onClick={() => sendAdHocRequest(endpoint)}>
                            Send Ad-Hoc Request
                        </Button>
                    </ListItem>
                ))}
            </List>
        </Container>
    );
}

export default App;
