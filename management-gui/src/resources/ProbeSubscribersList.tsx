'use client';
import {
    List,
    Datagrid,
    BooleanField,
    ReferenceField,
    EditButton, TextField,
} from 'react-admin';

export const ProbeSubscriberList = () => (
    <List>
        <Datagrid rowClick="edit">
            <ReferenceField source="probeId" reference="probes" label="Probe">
                <TextField source="name" />
            </ReferenceField>
            <ReferenceField source="subscriberId" reference="subscribers" label="Subscriber">
                <TextField source="name" />
            </ReferenceField>
            <BooleanField source="owner" />
            <BooleanField source="support" />
            <BooleanField source="customer" />
            <EditButton />
        </Datagrid>
    </List>
);
