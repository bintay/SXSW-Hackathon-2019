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
var server = require('http').Server(app);
var io = require('socket.io')(server);
const request = require('request');

app.all('/public/uploads/:file/', function(req, res, next) {
   res.header("Access-Control-Allow-Origin", "*");
   res.header("Access-Control-Allow-Headers", "X-Requested-With");
   next();
});

app.use('/public', express.static(path.join(__dirname, 'public')));
app.use(bodyParser.urlencoded({ extended: false }));
app.use(expressFile());

app.get('/', function (req, res) {
   res.sendFile(__dirname + '/view/index.html');
});

app.get('/message', function (req, res) {
   res.sendFile(__dirname + '/view/message.html');
});

app.get('/scout', function (req, res) {
   res.sendFile(__dirname + '/view/scout.html');
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
            console.log();
            request.post(`http://localhost:10051/issue-video?identifier=${req.params.name}&similarityScore=${score}`).on('response', function (response) {
               // pass
            });

            console.log(score);

            res.json({
               score: score,
               tips: tips
            });
         });
      }
   });
});

io.on('connection', function (socket) {
   console.log('connected' + new Date());
   socket.on('toUser', function (data) {
      data = JSON.parse(data);
      getTranslation(data.message, 'en', function (translated) {
         io.emit('toUser', JSON.stringify({
            name: data.name,
            message: translated
         }));
      });
   });

   socket.on('toGeorge', function (data) {
      data = JSON.parse(data);
      getTranslation(data.message, 'es', function (translated) {
         io.emit('toGeorge', JSON.stringify({
            name: data.name,
            message: translated
         }));
      });
   });
});

server.listen(PORT, function () {
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
   request.post(`http://localhost:5000/http://localhost:5678/public/uploads/${fileurl}`, {}, function (error, response, body) {
      if (error) console.log(error);
      console.log(body);
      body = JSON.parse(body);
      let result = {};
      result.score = body[1];
      result.tips = ['market on Spotify in Mexico'];
      callback(result.tips, result.score * 0.973);
   });
}
