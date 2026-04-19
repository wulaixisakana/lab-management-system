#!/bin/bash
npm run build
mkdir -p dist/functions
cp functions/_middleware.js dist/functions/
echo "Build completed with functions"
