function confirm_genre_delete(href, genre)
{
	check = confirm('Wollen Sie das Genre \"' + genre + '\" wirklich l�schen?');
	if(check == true)
		document.location.href=href;
}

function confirm_pfad_delete(href)
{
	check = confirm('Wollen Sie diesen Pfad wirklich l�schen?');
	if(check == true)
		document.location.href=href;
}