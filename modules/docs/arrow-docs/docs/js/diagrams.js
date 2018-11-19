function drawNomNomlDiagram(elementId, file) {
    var canvas = document.getElementById(elementId);
    var req = new Request(file);
    fetch(req)
      .then(function(response) { return response.text() })
      .then(function(nomlText) {
        nomnoml.draw(canvas, nomlText);
      });
}