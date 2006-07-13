#!/bin/sh

pg_dump --blobs --file=issuemgr-backup.db --format=c --ignore-version issuemgr

