$(document).ready(function() {
	jQuery("#user-grid").jqGrid({
	   	url:'../userManagement/',
		//url:'test.json',
		datatype: "jsonstring",
	   	colNames:['Login','First name', 'Last name', 'Email'],
	   	colModel:[
	   		{name:'login',index:'login', width:55,editable:false,editoptions:{readonly:true,size:10}},
	   		{name:'firstName',index:'firstName', width:80,editable:true,editoptions:{size:10}},
	   		{name:'lastName',index:'lastName', width:90,editable:true,editoptions:{size:25}},
	   		{name:'email',index:'email', width:60, align:"right",editable:true,editoptions:{size:10}}	   				
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
            repeatitems: false,
            cell: "cell",
            id: "id",
            userdata: "userdata",    
        } 
	}).jqGrid('navGrid','#user-grid-pager',
		{}, //options
		{height:280,reloadAfterSubmit:false}, // edit options
		{height:280,reloadAfterSubmit:false}, // add options
		{reloadAfterSubmit:false}, // del options
		{} // search options
	);
});