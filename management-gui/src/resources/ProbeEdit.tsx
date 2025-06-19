import {
    Edit,
    SimpleForm,
    TextInput,
    BooleanInput,
    NumberInput,
    SelectInput,
    ReferenceInput,
    AutocompleteInput
} from 'react-admin';

export const ProbeEdit = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="name" />
            <SelectInput source="environment" choices={[
                { id: 'local', name: 'local' },
                { id: 'dev', name: 'dev' },
                { id: 'test', name: 'dest' },
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
                <AutocompleteInput optionText="name" />
            </ReferenceInput>
            <ReferenceInput source="servicesId" reference="services">
                <AutocompleteInput optionText="name" />
            </ReferenceInput>
        </SimpleForm>
    </Edit>
);
