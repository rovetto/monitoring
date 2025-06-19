import { Create, SimpleForm, TextInput } from 'react-admin';

export const SubscriberCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="vorname" />
            <TextInput source="name" />
            <TextInput source="email" />
            <TextInput source="mobile" />
        </SimpleForm>
    </Create>
);
