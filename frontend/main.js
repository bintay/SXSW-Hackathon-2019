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

app.post('/submit/', type, function (req, res) {
   res.setHeader("Access-Control-Allow-Origin", "*");
   let name = uuid();
   
   let file = req.files.file;
   file.mv(__dirname + '/public/uploads/' + name, function(err) {
      if (err)
         return res.status(500).send(err);

      res.json({
         filename: name
      });
   });
});

app.listen(PORT, function () {
   console.log(`Listening on port ${PORT}`);
});
