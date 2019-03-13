const express = require('express');
const app = express();
const PORT = 5678 || process.argv[2];
const path = require('path');
var multer  = require('multer');
var upload = multer({ dest: __dirname + '/public/uploads/' });
var type = upload.single('data');
var exec = require('child_process').exec;
var fs = require('fs');
var bodyParser = require('body-parser');
var expressFile = require('express-fileupload');
const uuid = require('uuid/v1');

app.use('/public', express.static(path.join(__dirname, 'public')));
app.use(bodyParser.urlencoded({ extended: false }));
app.use(expressFile());

app.get('/', function (req, res) {
   res.sendfile(__dirname + '/view/index.html');
});

app.post('/submit/:name/:email/', type, function (req, res) {
   res.setHeader("Access-Control-Allow-Origin", "*");
   let name = uuid();
   console.log(req.params.name);
   console.log(req.files.file);
   
   let file = req.files.file;
   file.mv(__dirname + '/public/uploads/' + name, function(err) {
      if (err) {
         return res.status(500).send(err);
      } else {
         getFeedback (name, function (tips, score) {
            res.json({
               score: score,
               tips: tips
            });
         });
      }
   });
});

app.listen(PORT, function () {
   console.log(`Listening on port ${PORT}`);
});

function getTranslation (sentence, language, callback) {
   exec(`export GOOGLE_APPLICATION_CREDENTIALS=/Users/bent/gcloud/Hacklahoma-2019-cd5a0529ed91.json && curl -X POST \\
      -H "Authorization: Bearer "$(gcloud auth application-default print-access-token) \\
      -H "Content-Type: application/json; charset=utf-8" \\
      --data "{
         'q': '${sentence.replace(/'/, "\\'")}',
         'target': '${language}'
      }" "https://translation.googleapis.com/language/translate/v2"`, (err, stdout, stderr) => {
      if (err) console.log(err);
      callback(JSON.parse(stdout));
   });
}

function getFeedback (fileurl, callback) {
   setTimeout(() => {
      callback(["marketing on Spotify in Mexico"], Math.random());
   }, 2000)
}
