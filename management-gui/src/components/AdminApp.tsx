'use client';

import { Admin, Resource } from 'react-admin';
import jsonServerProvider from 'ra-data-json-server';
import { ProbeList } from '@/resources/ProbeList';
import { SubscriberList } from '@/resources/SubscriberList';
import { SubscriberEdit } from '@/resources/SubscriberEdit';
import { ProbeSubscriberList } from '@/resources/ProbeSubscribersList';
import {ProbeEdit} from "@/resources/ProbeEdit";
import {ProbeSubscriberEdit} from "@/resources/ProbeSubscriberEdit";
import {ProbeCreate} from "@/resources/ProbeCreate";
import {SubscriberCreate} from "@/resources/SubscriberCreate";
import {ProbeSubscriberCreate} from "@/resources/ProbeSubscriberCreate";

const dataProvider = jsonServerProvider('http://localhost:8092');

const AdminApp = () => (
    <Admin dataProvider={dataProvider}>
            <Resource
                name="probes"
                edit={ProbeEdit}
                create={ProbeCreate}
                list={ProbeList}
                recordRepresentation={(record) => `${record.name}`} />
            <Resource
                name="subscribers"
                edit={SubscriberEdit}
                create={SubscriberCreate}
                list={SubscriberList}
                recordRepresentation={(record) => `${record.name}`}/>
            <Resource
                name="probe-subscribers"
                edit={ProbeSubscriberEdit}
                create={ProbeSubscriberCreate}
                list={ProbeSubscriberList} />
    </Admin>
);

export default AdminApp;
