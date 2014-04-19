<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Sub-Topic Clustering on Tweets and generating brief pseudo summaries per cluster related to particular Entity</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<link rel="stylesheet" media="screen,projection" type="text/css" href="css/main.css" />
<link rel="stylesheet" media="screen,projection" type="text/css" href="css/skin.css" />
<script type="text/javascript" src="javascript/cufon-yui.js"></script>
<script type="text/javascript" src="javascript/font.font.js"></script>
<script type="text/javascript">
Cufon.replace('h1, h2, h3, h4, h5, h6, #logo', {
    hover: true
});
</script>
</head>
<body>
<!-- START PAGE SOURCE -->
<div id="main">
  <div id="header" class="box">
    <p id="logo">Sub-Topic Clustering on Tweets and generating brief pseudo summaries per cluster related to particular Entity</p>
  </div>
  <div id="nav" class="box">
    <ul>
      <li class="current"><a href="index.php">Home</a></li>
    </ul>
	<ul>
      <li class="current"><a href="description.html">Description</a></li>
    </ul>
    <ul>
      <li class="current"><a href="index.php">Slides</a></li>
    </ul>
    <ul>
      <li class="current"><a href="index.php">Video</a></li>
    </ul>
  </div>
  <div id="section" class="box">
    <div id="content">
      <div id="topstory" class="box">
        <h1><a href="http://nlp.uned.es/replab2013/" target="_blank">RepLab Tweet Clusters</a></h1>
      </div>
      <div class="padding">
        <ul class="articles box">
          <li class="box">
		    <div id="content">
                        <?php

                           $string = $_GET[page];
                           if($string == "")
                             $string = "a";
			   $list = glob("test/NEOut/" . $string . "*");
                           foreach ($list as $fn) {
                             $pos = strrpos($fn, "/");
                             if($pos === false) {
                                 echo "<p> Unexpected error </p>";
                             }
                             else {
                               $filename = substr($fn, $pos+1, strlen($fn));
                               $cluster_name = str_replace("_", " ", $filename);
			   	echo '<p><a href=showSummary.php?tweet=' . urlencode($filename) . '>' . $cluster_name . "</a></p>";
                             }                               
                           }

                           $string = strtoupper($string);
                           $list = glob("test/NEOut/" . $string . "*");
                           foreach ($list as $fn) {
                             $pos = strrpos($fn, "/");
                             if($pos === false) {
                                 echo "<p> Unexpected error </p>";
                             }
                             else {
                               $filename = substr($fn, $pos+1, strlen($fn));
                               $cluster_name = str_replace("_", " ", $filename);
			   	echo '<p><a href=showSummary.php?tweet=' . $filename . '>' . $cluster_name . "</a></p>";
                             }                               
                           }
                        ?>
			</div>
          </li>
        </ul>
          <div class="pagination box">
          <p class="f-right"> 
            <a href="index.php?page=a" class="current">A</a> 
	    <a href="index.php?page=b">B</a> 
            <a href="index.php?page=c">C</a> 
            <a href="index.php?page=d">D</a> 
            <a href="index.php?page=e">E</a> 
	    <a href="index.php?page=f">F</a> 
	    <a href="index.php?page=g">G</a> 
            <a href="index.php?page=h">H</a> 
            <a href="index.php?page=i">I</a> 
	    <a href="index.php?page=j">J</a> 
            <a href="index.php?page=k">K</a> 
            <a href="index.php?page=l">L</a> 
            <a href="index.php?page=m">M</a> 
	    <a href="index.php?page=n">N</a> 
	    <a href="index.php?page=o">O</a> 
            <a href="index.php?page=p">P</a> 
            <a href="index.php?page=q">Q</a>
 	    <a href="index.php?page=r">R</a> 
            <a href="index.php?page=s">S</a> 
            <a href="index.php?page=t">T</a> 
            <a href="index.php?page=u">U</a> 
	    <a href="index.php?page=v">V</a> 
	    <a href="index.php?page=w">W</a> 
            <a href="index.php?page=x">X</a> 
            <a href="index.php?page=y">Y</a>
            <a href="index.php?page=z">Z</a>
        </div>
      </div>
    </div>
      <div id="aside">
      <h3 class="title">References</h3>
      <div class="padding">
        <ul class="sponsors">
          <li class="first" ><a href="https://github.com/aritter/twitter_nlp">Aritter Parser</a><br />
            Git hub repo for Twitter aritter parser</li>
          <li><a href="http://timeml.org/site/tarsqi/toolkit/index.html">Tarsqi Toolkit</a><br />
            Time expression extractor tool kit</li>
		  <li><a href="http://nlp.stanford.edu:8080/ner/">Stanford Named Entity Tagger</a><br />
            Online tool for Stanford NER Tagger</li>
          <li class="last"><a href="https://github.com/ceteri/textrank">Text Rank</a><br />
            Text Rank implementation in java</li>
        </ul>
      </div>
    </div>
  </div>
  <div id="footer" class="box">
    <p class="f-right">Group 19, IIIT Hyderabad</p>
    <!--<p class="f-left">Copyright &copy;&nbsp;2011 <a href="#">CodaPress</a></p>-->
  </div>
</div>
<!-- END PAGE SOURCE -->
<div align=center>By - Anil Kumar Sutrala, Dinesh Singla, Raghav K, Snigdha Verma</div></body>
</html>
