const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

console.log('Building frontend...');
execSync('npm run build', { stdio: 'inherit' });

console.log('Copying functions to dist...');
const functionsDir = path.join(__dirname, 'functions');
const distFunctionsDir = path.join(__dirname, 'dist', 'functions');

if (!fs.existsSync(distFunctionsDir)) {
  fs.mkdirSync(distFunctionsDir, { recursive: true });
}

if (fs.existsSync(functionsDir)) {
  const files = fs.readdirSync(functionsDir);
  files.forEach(file => {
    fs.copyFileSync(
      path.join(functionsDir, file),
      path.join(distFunctionsDir, file)
    );
  });
}

console.log('Build completed with functions!');
