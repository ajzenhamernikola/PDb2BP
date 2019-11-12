const readline = require('readline').createInterface(
    process.stdin,
    process.stdout
);

const symbolConverter = require('./symbol-converter');
const path = require('./path');

const question = 
`Odaberite skript za izvrsavanje:
    1. TFE (translate from TeX)
    2. EXIT (exits the program)\n\n`;

readline.question(question, (answer) => {
    if (answer.toLowerCase() === 'tfe') {
        const pathToTranslate = __dirname + '/../poglavlja/';
        path.iterateAndTranslate(pathToTranslate, symbolConverter.translateFromTexEscape);
    } else if (answer.toLowerCase() === 'exit') {
        timeToExit = true;
    } else {
        console.log('Unknown option...');
    }

    process.exit(0);
});