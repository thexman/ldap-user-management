var userWebService = {
	url : '../userManagement/',	
	changePassword: function(userId, oldPassword, newPassword) {
		var password = "";
		var params = {
			"method": "changePassword", 
			"userId": userId,			
			"oldPassword": oldPassword,
			"newPassword": newPassword
		};
		var success = function(data) {
			var dlg = $("#dialog-message");
			var html = "";
			if (!data.success) {
				
				html += '<p><span class="ui-icon ui-icon-circle-close" style="float: left; margin: 0 7px 50px 0;"></span>' + 
				 '<b>Error changing password.</b></p><p>' + data.description + '</p>';
				
			} else {
				html += '<p><span class="ui-icon ui-icon-circle-close" style="float: left; margin: 0 7px 50px 0;"></span>' + 
				 '<b>' + data.description + '</b></p>';
			}
			dlg.find(".dialog-text").html(html)
			dlg.dialog("open");
		};
		
		var error = function(data) {
			var dlg = $("#dialog-message");
			var html = '<p><span class="ui-icon ui-icon-circle-close" style="float: left; margin: 0 7px 50px 0;"></span>' + 
				 '<b>Error changing password.</b></p><p>Cannot connect to backend system</p>';
			dlg.find(".dialog-text").html(html)
			dlg.dialog("open");
		};
		
		$.ajax({
			url: this.url, 
			data: params, 
			success: success,
			error: error,
			dataType: "json" 
		});
		
		
	}	
};

var ui = {
	updateDialogTips: function (tips, text) {		
		tips.text(text).addClass( "ui-state-highlight" );
		setTimeout(function() {
			tips.removeClass( "ui-state-highlight", 1500 );
		}, 500 );
	},
	
	changePassword: function(userId) {
		$("#change-password-dialog").data("userId", userId);
		$("#change-password-dialog").dialog("open");
	}
}

$(document).ready(function() {
	$("#user-grid").jqGrid({
	   	url: userWebService.url,
		//url:'test.json',
	   	datatype: "json",
	   	colNames:['Login','First name', 'Last name', 'Email', ''],
	   	colModel:[
	   		{name:'login',index:'login', width:250,editable:false,editoptions:{readonly:true,size:10}},
	   		{name:'firstName',index:'firstName', width:100,editable:true,editoptions:{size:10}},
	   		{name:'lastName',index:'lastName', width:100,editable:true,editoptions:{size:25}},
	   		{name:'email',index:'email', width:250, align:"right",editable:true,editoptions:{size:10}},	   				
	   		{name:'changePassword',index:'changePassword', width:120, align:"right",editable:false}
	   	],
	   	rowNum:10,
	   	rowList:[10,20,30],
	   	pager: '#user-grid-pager',
	   	sortname: 'login',
	    viewrecords: true,
	    sortorder: "desc",
	    caption:"Users list",
	    editurl:"someurl.php",
		height:210,
		jsonReader : {
			root: "rows",
			page: "page",
			total: "total",
			records: "records",
			repeatitems: true,
			cell: "cell",
			id: "id",
			userdata: "userdata",
			subgrid: {
				root:"rows", 			
				repeatitems: true, 
				cell:"cell"
			}
		},
		gridComplete: function() {
			var grid = $("#user-grid");
	    	var ids = grid.getDataIDs(); 
            for(var i=0; i < ids.length; i++){ 
	            var id = ids[i];
	            var dataId = "data-id='" + id + "'";	            
	            var htmlBtn = "<input type='button' " + dataId + " value='Change password' onclick=\"ui.changePassword('" + id + "')\"></input>"; 
	            grid.setRowData( ids[i], { changePassword:  htmlBtn } );
            } 
	    },
	}).jqGrid('navGrid','#user-grid-pager',
		{}, //options
		{height:280,reloadAfterSubmit:false}, // edit options
		{height:280,reloadAfterSubmit:false}, // add options
		{reloadAfterSubmit:false}, // del options
		{} // search options
	);
	
	$("#dialog-message").dialog({
		autoOpen: false,
		modal: true,
		buttons: {
			Ok: function() {
				$(this).dialog("close");
			}
		}
	});
		
	$("#change-password-dialog").dialog({
		autoOpen: false,
		height: 400,
		width: 350,
		modal: true,
		buttons: {
			"Change password": function() {				
				var oldPassword = $("#oldPassword");
			    var newPassword = $("#newPassword");
			    var newPassword2 = $("#newPassword2");
			    var allFields = $( [] ).add( oldPassword ).add( newPassword ).add( newPassword2 );
			    var tips = $("#change-password-dialog .validateTips");
				allFields.removeClass("ui-state-error");
 				
			    var valid = true;

			    if (oldPassword.val().length < 1) {
			    	ui.updateDialogTips(tips, "Please enter old password!");
			    	oldPassword.addClass("ui-state-error");			    	
			    	return;
			    }
			    
			    if (newPassword.val().length < 4) {
			    	ui.updateDialogTips(tips, "Pasword must be at least 4 characters long");
			    	newPassword.addClass("ui-state-error");
			    	return;
			    }
			    
			    if (newPassword.val() !== newPassword2.val()) {
			    	ui.updateDialogTips(tips, "Password confirmation doesnt' match");
					newPassword2.addClass("ui-state-error");					
					return;
				}
			    
			    var userId = $("#change-password-dialog").data("userId");
			    var oldPass = oldPassword.val();
			    var newPass = newPassword.val();
			    
			    oldPassword.val("");
			    newPassword.val("");
			    newPassword2.val("");
			    
			    $(this).dialog("close");
			    
			    userWebService.changePassword(userId, oldPass, newPass);  			    
	        },
	        Cancel: function() {
	        	var oldPassword = $("#oldPassword");
			    var newPassword = $("#newPassword");
			    var newPassword2 = $("#newPassword2");
			    
			    oldPassword.val("");
			    newPassword.val("");
			    newPassword2.val("");
	        	$(this).dialog("close");
	        }
		},
		close: function() {
			$(this).find(".ui-state-error").removeClass("ui-state-error");
		}
	});
});