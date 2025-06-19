import {Create, SimpleForm, TextInput, BooleanInput, NumberInput, SelectInput, ReferenceInput} from 'react-admin';

export const ProbeCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="name" />
            <SelectInput source="environment" choices={[
                { id: 'local', name: 'local' },
                { id: 'dev', name: 'dev' },
                { id: 'test', name: 'test' },
                { id: 'prod', name: 'prod' },
                { id: 'none', name: 'none' },
            ]} />
            <BooleanInput source="maintenance" />
            <NumberInput source="port" />
            <TextInput source="rule" />
            <TextInput source="url" />
            <TextInput source="remark" />
            <BooleanInput source="checkHttp" />
            <BooleanInput source="checkCertificate" />
            <BooleanInput source="checkRule" />
            <ReferenceInput source="serverId" reference="servers">
                <SelectInput optionText={(record) => `${record.name}`} />
            </ReferenceInput>
            <ReferenceInput source="serviceId" reference="services">
                <SelectInput optionText={(record) => `${record.name}`} />
            </ReferenceInput>
        </SimpleForm>
    </Create>
);
