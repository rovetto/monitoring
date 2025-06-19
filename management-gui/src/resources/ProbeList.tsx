import {
    List,
    Datagrid,
    TextField,
    BooleanField,
    NumberField,
    EditButton, ReferenceField,
} from 'react-admin';

export const ProbeList = () => (
    <List>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <BooleanField source="checkHttp" />
            <BooleanField source="checkCertificate" />
            <BooleanField source="checkRule" />
            <TextField source="environment" />
            <BooleanField source="maintenance" />
            <TextField source="url" />
            <NumberField source="port" />
            <TextField source="rule" />
            <TextField source="remark" />
            <EditButton />
            <ReferenceField source="serverId" reference="servers" label="Server">
                <TextField source="name" />
            </ReferenceField>
            <ReferenceField source="serviceId" reference="services" label="Service">
                <TextField source="name" />
            </ReferenceField>
        </Datagrid>
    </List>
);
