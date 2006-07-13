#!/bin/sh

pg_dump -U movielib --blobs --file=movielib-200602.db --format=c --ignore-version movielib

