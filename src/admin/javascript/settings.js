/* Validation functions for settings.jsp */
/* $Revision$, $Date$ */

function updateSpecials()
{
    var form = document.forms["settings"];
    var sum = 0;

    sum += parseInt(form.addLine.value);
    sum += parseInt(form.clearLine.value);
    sum += parseInt(form.nukeField.value);
    sum += parseInt(form.randomClear.value);
    sum += parseInt(form.switchField.value);
    sum += parseInt(form.clearSpecial.value);
    sum += parseInt(form.gravity.value);
    sum += parseInt(form.quakeField.value);
    sum += parseInt(form.blockBomb.value);

    var sumElement = document.getElementById("specials.total");
    sumElement.innerHTML = sum;
    sumElement.style.color = (sum == 100) ? "black" : "red";
}

function updateBlocks()
{
    var form = document.forms["settings"];
    var sum = 0;

    sum += parseInt(form.line.value);
    sum += parseInt(form.square.value);
    sum += parseInt(form.leftL.value);
    sum += parseInt(form.rightL.value);
    sum += parseInt(form.leftZ.value);
    sum += parseInt(form.rightZ.value);
    sum += parseInt(form.halfcross.value);

    var sumElement = document.getElementById("blocks.total");
    sumElement.innerHTML = sum;
    sumElement.style.color = (sum == 100) ? "black" : "red";
}
