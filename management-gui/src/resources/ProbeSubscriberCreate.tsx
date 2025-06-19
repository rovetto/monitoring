import { Create, SimpleForm, BooleanInput, ReferenceInput, SelectInput } from 'react-admin';

export const ProbeSubscriberCreate = () => (
    <Create>
        <SimpleForm>
            <ReferenceInput source="probeId" reference="probes">
                <SelectInput optionText="name" />
            </ReferenceInput>
            <ReferenceInput source="subscriberId" reference="subscribers">
                <SelectInput optionText={(record) => `${record.name} ${record.vorname}`} />
            </ReferenceInput>
            <BooleanInput source="owner" />
            <BooleanInput source="support" />
            <BooleanInput source="customer" />
        </SimpleForm>
    </Create>
);
