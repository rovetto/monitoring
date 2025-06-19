'use client';
import {
    List,
    Datagrid,
    TextField,
    EditButton,
} from 'react-admin';

export const SubscriberList = () => (
    <List>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <TextField source="vorname" />
            <TextField source="email" />
            <TextField source="mobile" />
            <EditButton />
        </Datagrid>
    </List>
);
