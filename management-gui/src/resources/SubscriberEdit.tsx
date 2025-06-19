import { Edit, SimpleForm, TextInput } from 'react-admin';

export const SubscriberEdit = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="vorname" />
            <TextInput source="name" />
            <TextInput source="email" />
            <TextInput source="mobile" />
        </SimpleForm>
    </Edit>
);
