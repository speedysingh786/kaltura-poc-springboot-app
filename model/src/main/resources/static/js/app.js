var APP_NAMESPACE = "http://localhost:7070";

var loadingOption = {message: 'Please Wait..'};

function init() {

    redirectToLogin();
}

function redirectToLogin() {

    window.location.replace("/login.html");
}

function logininit() {

    $("#loginbutton").on('click', function () {

        $(document.body).loading(loadingOption);

        $('#errorTextDiv').empty();

        var username = $("#username").val();
        var password = $("#password").val();

        var userData = JSON.stringify({
            "username": username,
            "password": password
        });

        $.ajax({
            url: APP_NAMESPACE + "/validateLogin",
            method: "POST",
            contentType: "application/json",
            dataType: "json",
            data: userData,
            success: function (result) {

                if (result.status === "Success") {

                    $(document.body).loading('stop');
                    window.location.replace("/home.html");
                } else {

                    $("#errorTextDiv").append("<span class='errorText'>" + result.status + "</span>");
                    $(document.body).loading('stop');
                }

            }
        });
    });
}

function logout() {

    $(document.body).loading(loadingOption);

    $.ajax({
        url: APP_NAMESPACE + "/logout",
        method: "GET",
        success: function (result) {

            $(document.body).loading('stop');
            redirectToLogin();
        }
    });
}

function homeinit() {

    loadArtefacts();

    $('#searchButton').on('click', function () {
        loadArtefacts();
    });

    $('#inputGroupFile01').on('change',function(){
        var fileName = $(this)[0].files[0].name;
        $(this).next('.custom-file-label').html(fileName);
    });

    $('#uploadFileButton').on('click', function () {

        $("#uploadDiv").loading(loadingOption);

        /*var allowedFileExtensions = ['pdf', 'mp4', 'mov'];
        if ($.inArray($('#inputGroupFile01').val().split('.').pop().toLowerCase(), allowedFileExtensions) == -1) {

            $("#commonDialogueTextDiv").empty();
            $("#commonDialogueTextDiv").append('<h6 class="modal-title" id="commonDialogueTitle">Only ' + allowedFileExtensions + ' file extensions are allowed.</h6>');

            $('#commonDialogue').modal({
                keyboard: false
            });

            $('#inputGroupFile01').val(null);
            $('#inputGroupFile01').next('.custom-file-label').html('');
            $("#uploadDiv").loading('stop');

            return;
        }*/

        var documentData = new FormData();
        documentData.append('file', $('#inputGroupFile01')[0].files[0]);
        $.ajax({
            url: APP_NAMESPACE + "/app/upload",
            type: 'POST',
            data: documentData,
            cache: false,
            contentType: false,
            processData: false,
            success: function (response) {

                $('#inputGroupFile01').val(null);
                $('#inputGroupFile01').next('.custom-file-label').html('');
                $("#uploadDiv").loading('stop');
                loadArtefacts();
            }
        });

    });
}

function loadArtefacts() {

    $("#artefactsDiv").loading(loadingOption);

    $("#artefactsDiv").empty();

    var searchRequestData = JSON.stringify({
        "object": $("#searchInput").val()
    });

    $.ajax({
        url: APP_NAMESPACE + "/app/artefacts",
        method: "POST",
        contentType: "application/json",
        dataType: "json",
        data: searchRequestData,
        success: function (result) {

            for (var i = 0; i < result.length; i++) {

                $("#artefactsDiv").append(getMediaMarkup(result[i].name, result[i].id, result[i].mediaType, result[i].sizeInBytes, null, result[i].transcodeComplete, false));

            }

            $('[data-toggle="tooltip"]').tooltip();//bootstrap tooltip

            $("#artefactsDiv").loading('stop');
        }
    });
}

function getMediaMarkup(name, id, mediaType, sizeInBytes, urlKey, transcodeComplete, toOpenMedia) {

    var toShowTitle = false;
    var originalName = name;
    if (name.length > 10) {
        name = name.substring(0,9) + "..";
        toShowTitle = true;
    }

    var sizeDisplay = sizeInBytes + " b";
    if (sizeInBytes > 1024) {
        var sizeInKB = Math.floor(sizeInBytes/1024);
        sizeDisplay = sizeInKB + " kb";
    } else if (sizeInKB > 1024 * 1024) {
        var sizeInMB = Math.floor(size/1024);
        sizeDisplay = sizeInMB + " mb";
    }

    var randomRGBColor = getRandomBGColor();

    var actionString = "#";
    if (transcodeComplete) {
        actionString = toOpenMedia ? "javascript:openMedia('" + urlKey + "')" : "javascript:loadArtefactDetails('" + id + "')";
    }

    var markup =
        '<a href="' + actionString + '">' +
        '<div class="card artefactitem">' +
        '<div class="card-body" style="padding:0">' +

        '<div class="display-flex">' +

        '<div class="artefactitemicon" style="border:1px solid rgb(' + randomRGBColor +  '); border-right-style: none; background-color:rgba(' + randomRGBColor +',0.6);">' +
        getFontAwesomeTag(mediaType, transcodeComplete) +
        '</div>' +

        '<div class="artefactitemtext" ' + (toShowTitle ? 'title="' + originalName + '" data-toggle="tooltip"' : '')+ 'style="border:1px solid rgb(' + randomRGBColor +  '); border-left-style: none; background-color:rgba(' + randomRGBColor +',0.5);">' +
        '<h6 class="card-subtitle mb-2">' + name + '</h6>' +
        '<h6 class="card-subtitle mb-2">' + sizeDisplay + '</h6>' +
        '</div>' +

        '</div>' +

        '</div>' +
        '</div>' +
        '</a>';

    return markup;
}

function getFontAwesomeTag(mediaType, transcodeComplete) {

    if (!transcodeComplete) {
        return '<i class="fas fa-spinner fa-spin fa-3x"></i>';
    }

    var faTag;

    switch (mediaType) {

        case 'HTML'         : faTag = '<i class="fas fa-file-code fa-3x"></i>'; break;
        case 'PDF'          : faTag = '<i class="fas fa-file-pdf fa-3x"></i>'; break;
        case 'ZIP'          : faTag = '<i class="fas fa-file-archive fa-3x"></i>'; break;
        case 'TXT'          : faTag = '<i class="fas fa-file-alt fa-3x"></i>'; break;
        case 'WORD'         : faTag = '<i class="fas fa-file-word fa-3x"></i>'; break;
        case 'EXCEL'        : faTag = '<i class="fas fa-file-excel fa-3x"></i>'; break;
        case 'POWERPOINT'   : faTag = '<i class="fas fa-file-powerpoint fa-3x"></i>'; break;
        case 'IMAGE'        : faTag = '<i class="fas fa-image fa-3x"></i>'; break;
        case 'AUDIO'        : faTag = '<i class="fas fa-file-audio fa-3x"></i>'; break;
        case 'VIDEO'        : faTag = '<i class="fas fa-video fa-3x"></i>'; break;
        case 'OTHER'        : faTag = '<i class="fas fa-file fa-3x"></i>'; break;
        default             : faTag = '<i class="fas fa-file fa-3x"></i>';
    }

    return faTag;
}

function getRandomBGColor() {

    var colors = ['153, 180, 51',
        '0, 163, 0',
        '30, 113, 69',
        '159, 0, 167',
        '126, 56, 120',
        '96, 60, 186',
        '29, 29, 29',
        '0, 171, 169',
        '45, 137, 239',
        '43, 87, 151',
        '255, 196, 13',
        '227, 162, 26',
        '218, 83, 44',
        '238, 17, 17',
        '185, 29, 71',
        '142, 142, 147',
        '255, 204, 0',
        '164, 196, 0',
        '96, 169, 23',
        '27, 161, 226',
        '106, 0, 255',
        '170, 0, 255',
        '240, 163, 10',
        '100, 118, 135',
        '118, 96, 138'
    ];

    var randomIndex = Math.floor(Math.random() * 100 ) % colors.length;

    return colors[randomIndex];
}

function openMedia(key) {

    $(document.body).loading(loadingOption);

    var mediaRequestData = JSON.stringify({
        "key": key
    });
	var fileType = key.split(".").reverse()[0];
    $.ajax({
        url: APP_NAMESPACE + "/app/get/secure/url",
        method: "POST",
        contentType: "application/json",
        dataType: "json",
        data: mediaRequestData,
        success: function (result) {

            if (result.url) {

                $(document.body).loading('stop');
                //window.open(result.url, '_blank');
				$("#artefactLinks").modal('hide');
				if(fileType == 'jpeg')
					showPreviewPopup(result.url);
				else
					window.open(result.url, '_blank');
            }
        }
    });
}
function showPreviewPopup(url){
	$("#imagePreview img").attr('src',url);
	$('#imagePreview').modal({
        keyboard: false
    });
}
function showVideoPopup(url){
	$("#videoPreview video source").attr('src',url);
	$('#videoPreview').modal({
        keyboard: false
    });
}
function loadArtefactDetails(artefactID) {

    $("#originalArtefact").empty();
    $("#transcodedArtefacts").empty();

    $("#artefactLinks").loading(loadingOption);

    $('#artefactLinks').modal({
        keyboard: false
    });

    var artefactData = JSON.stringify({
        "id": artefactID
    });

    $.ajax({
        url: APP_NAMESPACE + "/app/artefact",
        method: "POST",
        contentType: "application/json",
        dataType: "json",
        data: artefactData,
        success: function (result) {

            $("#originalArtefact").append(getMediaMarkup(result.name, result.id, result.mediaType, result.sizeInBytes, result.urlKey, true, true));

            if (result.transcodedArtefacts && result.transcodedArtefacts.length) {
                for (var i = 0; i < result.transcodedArtefacts.length; i++) {
                    var transcodedArtefact = result.transcodedArtefacts[i];
                    $("#transcodedArtefacts").append(getMediaMarkup(transcodedArtefact.name, transcodedArtefact.id, transcodedArtefact.mediaType, transcodedArtefact.sizeInBytes, transcodedArtefact.urlKey, true, true));
                }
            }

            $("#artefactLinks").loading('stop');
        }
    });
}
