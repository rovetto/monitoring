import {Edit, SimpleForm, BooleanInput, ReferenceInput, AutocompleteInput} from 'react-admin';

export const ProbeSubscriberEdit = () => (
    <Edit>
        <SimpleForm>
            <ReferenceInput source="probeId" reference="probes">
                <AutocompleteInput optionText="name" />
            </ReferenceInput>
            <ReferenceInput source="subscriberId" reference="subscribers">
                <AutocompleteInput optionText="name" />
            </ReferenceInput>
            <BooleanInput source="owner" />
            <BooleanInput source="support" />
            <BooleanInput source="customer" />
        </SimpleForm>
    </Edit>
);
