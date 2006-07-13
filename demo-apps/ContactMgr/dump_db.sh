#!/bin/sh

pg_dump -U contactmgr --blobs --file=contactmgr-200511.db --format=c --ignore-version contactmgr

