/* Functions for server.jsp */
/* $Revision$, $Date$ */

function shutdown()
{
    if (confirm("Do you really want to shut the server down?"))
    {
        location = "/servlet/net.jetrix.servlets.ServerAction?action=shutdown";
    }
}

function reloadFrame(id)
{
    var frame = document.getElementById(id);
    frame.src = frame.src;

    setTimeout("reloadFrame('" + id + "')", 10000);
}
