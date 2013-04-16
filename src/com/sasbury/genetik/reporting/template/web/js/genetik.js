
var genetik = (function (window) {
    
    //private vars
    var lib = {};
    
    //public vars
    lib.configurationData = null;
    lib.runs = null;
    lib.runData = {};
    lib.popKeyToFriendly = {
        
        fit:'Fitness',
        gen: 'Generation',
        genes: 'Genes',
        id: 'Id',
        p1: 'Parent One',
        p2: 'Parent Two',
        raw: 'Raw Scores',
        user: 'User Data',
    };
        
    function internalizeLinks()
    {
    	$('a.internal').each(function(index,value){
		
    		$(this).click(function(){
		
    			var url = $(this).attr('href');
                url = url.replace(/^.*#/, '');
                $.history.load(url);
                return false;
            });
    	});
    }

    function refreshToc()
    {
        $('#results_header').empty();
        $('#toc_list').empty();
    
    	$('#results_header').append("Genetik Results - "+genetik.configurationData['Run Date']);
        $('#toc_list').append('<li id="summary"><a href="#summary">Summary</a></li>');

        $('#toc_list').append('<li id="configuration"><a href="#configuration">Configuration</a></li>');
        
        for(var i = 0;i<lib.runs.length;i++)
        {
            var runName=lib.runs[i];
    
            $('#toc_list').append('<li id="'+runName+'"><a href="#run='+runName+'">'+runName+'</a></li>');
        }
    }
    
    function createLoadGen(runName,gen)
    {
        return function(data){
            lib.runData[runName]['generations'][gen] = data;
        };
    }
    
    function createLoadRun(runName,refresh)
    {
        return function(data){
		    lib.runData[runName]['generationStatistics'] = data['generationStatistics'];
    		lib.runData[runName]['runStatistics'] = data['runStatistics'];

		    if(refresh)
		    {
		        var hash = window.location.hash;

                hash = hash.replace("#","");
                
                if(!hash || hash.length===0)
                {
		            $.history.load("summary");
                }
                else
                {
                    lib.refresh();
                }
		    }
		};
    }
    
    lib.loadData = function()
    {
        jQuery.getJSON('run.json', function(data)
        {
    		lib.configurationData = data;
		
            var runString = lib.configurationData['runs'];

            if(runString == null)
            {
                runString = "run_zero";
            }

            lib.runs = runString.split(",");
        
            //set up the run data and load generations
            for(var i = 0;i<lib.runs.length;i++)
            {
                var runName = lib.runs[i];
                var generations = data[runName+'.generations'];
	    
    	        lib.runData[runName] = {};
    	        lib.runData[runName]['generations'] = [];
	        
    	        for(var j=0;j<generations;j++)
        	    {
        	        var gen = j;
        	        var success = createLoadGen(runName,gen);
        	        
        	        $.ajax({
                    		url: runName+'/generation.'+gen+'.json',
                    		dataType: 'json',
                    		mode: 'sync',
                    		success: success
                    	});
        	    }
            }
        
            //load stats and init page on last one
            for(var i = 0;i<lib.runs.length;i++)
            {
                var runName = lib.runs[i];
                var refresh = i==lib.runs.length-1;
                var success = createLoadRun(runName,refresh);
                
                $.ajax({
                		url: runName+'/statistics.json',
                		dataType: 'json',
                		mode: 'sync',
                		success: success
                	});
            }
    	});
    }
    
    lib.calculateName = function(type)
    {
        var hash = window.location.hash;
        var name = undefined;
    
        if(hash)
        {
            hash = hash.replace(/%20/g, ' ');
            var matcher = new RegExp(type+"=([^\/]+)");
            var matches = matcher.exec(hash);
            if(matches) name=matches[1];
        }
    
        return name;
    }
    
    lib.findIndividual = function(runName,gen,id)
    {
        var curRun = lib.runData[runName];
        var population = curRun['generations'][gen].pop;
        var retVal = null;
        
        for(var i=0,max=population.length;i<max;i++)
        {
            val = population[i];
            
            if(val['id'] == id)
            {
                retVal = val;
                break;
            }
        }
        
        return retVal;
    }

    lib.refresh = function()
    {
        if(!lib.configurationData)
        {
            lib.loadData()
            return;
        }
    
        var hash = window.location.hash;

        hash = hash.replace("#","");
    
        if(!hash || hash.length===0)
        {
            return;
        }

        refreshToc();
    
        var run = lib.calculateName("run");
        var id = lib.calculateName("id");
        
        if(run) $("#"+run).addClass('current');
        else $("#"+hash).addClass('current');
    
        if(hash == "summary")
        {
            $('#main_area').load('web/summary.html');
        }
        else if(hash == "configuration")
        {
            $('#main_area').load('web/configuration.html');
        }
        else if(id)
        {
            $('#main_area').load('web/individual.html');
        }
        else if(run)
        {
            $('#main_area').load('web/run.html');
        }
    
    	internalizeLinks();
    }
 
    return lib; 
}(window));