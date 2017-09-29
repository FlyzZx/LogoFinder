# LogoFinder
## Objectif	du	projet
L’objectif	est	de	développer	une	application	Android	capable d’identifier	un	logo	d’une	marque	
dans	 une	 photo capturée	 (ou	 récupérée) par	 un	 smartphone.	 L’application	 peut	 servir	 par	
exemple	pour	guider	l’utilisateur	vers le	site	web	concerné par	la	marque	ou	fournir	certaines	
informations correspondantes. Ce type d'applications supportant	la	recherche	d’information	à	
partir	d ‘une	photo	est très	populaire dans l'industrie	du	mobile.
La	réalisation	de	l’application	va	se	dérouler	en	3 parties :	
- La	 première	 partie	 sera	 consacrée	 à	 la	 mise	 en	 œuvre	 d’une	 version	 simplifiée	 de	
l’application.	Dans	cette	version,	la	reconnaissance	d’un	logo	dans	une	photo se	fera	par	
détection	de	caractéristiques	et	leur	comparaison	à	ceux	d’une	base	d’images prédéfinies
et	de	référence.	
- La	 deuxième partie	 consistera	 à	 étendre	 la	 version	 initiale	 pour	 utiliser	 une	 base	
d’images	hébergée	par	un	serveur	distant.	Dans	cette	version,	la	reconnaissance	d’image	
se	fera	à	l’aide	d’une	méthode	d’apprentissage	automatique	(classification).
- La	troisième	partie	est	très	semblable	à	la	deuxième	mais	la	différence	est	que l’objectif	
ici	non	seulement	de	trouver	l’identité	(la	classe	d’appartenance)	de	l’objet	mais	plutôt	
de	réaliser	un	moteur	de	recherche.	Ce	dernier	est	capable	de	trouver	les	objets	(images)	
les	 plus	 semblables	 à	 l’objet	 (image)	 requête.	 L’ordre	 d’affichage	 d’images	 dépend	 des	
distances	de	similarité	entre	l’image	test	et	la	requête	(alors	pas	d’apprentissage)

## Première	version	de	l’application
L’objectif	de	cette	première	 version	est	d’une	part	de	 vous familiariser	avec	le	développement	
d’une	application	mobile	et	d’autre	part	de	mettre	en	place	l’interface	utilisateur	pour	la	version	
suivante. Au	niveau	de	la	reconnaissance	de	logos,	le	traitement	à	réaliser	dans	cette	version	est	
volontairement	simplifié	pour	se	concentrer	sur	les	problèmes	d’interface.	
Fonctionnement	de	l’application
Le	fonctionnement	de	l’application	et	l’enchainement	des	écrans	sont	les suivants.		
Le	 premier	 écran	 est	 destiné	 au	 choix	 de	la	 photo à	 analyser.	Deux	 possibilités	 sont	 à offrir	 à	
l’utilisateur	pour	 choisir	 cette photo :	 
- soit	l’utilisateur	 sélectionne	 une	photo existante	 dans	la	
base	 du	 téléphone.	 
- soit	 l’utilisateur	 réalise	 une	 nouvelle	 photo.	 Dans	 les	 deux	 cas,	 un écran	
spécifique	est	à prévoir	pour	réaliser	cette	opération.	
Une	 fois	 la	 photo	 sélectionnée, l’application	 effectue le	 traitement	 de	 reconnaissance.	 Ce	
traitement	qui	est	expliqué	par	la	suite	se	décompose	en	deux	étapes.	La	première	étape	consiste	
à	 détecter	 des	 points	 clés caractéristiques	 dans	 la	 photo choisie	 par	 l’utilisateur.	 La	 seconde	
étape	consiste	à	apparier ces	points	caractéristiques	à	ceux	des	images	contenues	dans	la	base	
de	référence	de	 façon	à identifier	celles qui	sont	les	plus	proches (c.-à-d.	celle	qui	possèdent	le	
plus	grand	nombre	de points	caractéristiques	communs). 
Quand	 le	 traitement	 de	 reconnaissance	 est	 terminé, le	 résultat	 obtenu	 (c-à-d	 les images	 de	
références	les	plus	proches)	est	présenté	à	l’utilisateur	dans	un	nouvel	écran.	Pour	cet	écran,	il	
faut	 prévoir	 la	 présentation	 de	 plusieurs	 images et	 la	 possibilité	 d’aller	 sur	 le	 site	 web	
correspondant	en	cliquant	sur	une	de	ces	images.	

### Module	de	reconnaissance	d’images
Comme	 indiqué	 précédemment,	 	 le	 traitement	 de	 reconnaissance	 de	 logos	 intégré	 dans	 cette	
version	requiert	deux	opérations :	la	détection	des	points	d’intérêt	et	l’appariement	des	points
caractéristiques.	
En	vision	par	ordinateur,	la	détection	de	points	d'intérêt dans	une	image	numérique	(feature	
detection)	 consiste	 à	 mettre	 en	 évidence	 des	 zones	 présentant	 des	 propriétés	 locales	
remarquables	 (singulières).	 Ainsi,	 les	 coins	 sont	 les	 points	 de	 l'image	 où	 le	 contour	 change	
brutalement	de	direction.	Il	s'agit	de	points	particulièrement	stables et	donc	intéressants	pour	la	
répétabilité.	
La	plupart	des	techniques	de	détection	de	points	d'intérêt	sont	basées	sur	une	analyse	locale	de	
l'image.	 Nous	 pouvons	 par	 exemple	 citer	 le	 détecteur	 de	 Harris [1],	 les	 méthodes	 basées	 sur	
l'analyse	des	DoG	(Difference	of	Gaussians)	[2]	et	des	DoH	(Difference	of	Hessians)	[3].
Pour	 certaines	 applications,	 après	 la	 détection	 des	 points	 dans	 une	 image,	 on	 applique	 un	
algorithme	d'extraction	de	caractéristiques.	Cela	consiste	à	calculer	sur	chaque	zone	détectée	ce	
que	l'on	appelle	un	vecteur	caractéristique,	qui	décrit	le	contenu	de	la	zone	en	question,	sous	un	
point	 de	 vue	 particulier.	 Parmi	 les	 méthodes	 populaires,	 on	 peut	 citer	 Scale-invariant	 feature	
transform	 (SIFT)	développé	 par	David	 Lowe 2004 [2]	 et	 Speeded	Up	 Robust	 Features	 (SURF)	
développé	 par	Bay	et	Al,	 2006 [4],	et	 ceux	incluent	 un	 descripteur	 de	 région	 d'intérêt	en	 plus	
d'un	détecteur.	Les	détails	de	ces	méthodes	sont	exposés	dans	les	cours.	
Les	vecteurs	caractéristiques	constituent	une	façon	de	décrire	numériquement	le	contenu	d'une	
image	(l'orientation	de	l'arête	ou	la	magnitude	du	gradient	au	point	d'intérêt).	De	ce	fait,	ils	sont	
utilisés	efficacement	par	des	algorithmes	de	comparaison	d'images	ou	la	recherche	d'images	par	
le	contenu.

L’appariement	des	points	caractéristiques (Feature	matching)	est	à	la	base	de	beaucoup	de	
problèmes de	vision	par	ordinateur,	telles	que	la	reconnaissance de	l'objet.	
La	méthode	d’appariement	la	plus	courante	(souvent	utilisée	pour	des	points	SIFT)	repose	sur	le	
calcul	des distances	euclidiennes	dans	l’espace	de	descripteurs	 (espace	de	dimension	 128).	 La	
méthode	se	base	sur	la	construction	d’un	arbre	graphique	"k-d	tree"	et	sur	l’emploi	l’algorithme	
"Best	 Bin	 First".	 Ce	 dernier	 est	 capable	 de	 trouver	 les	 plus	 proches	 voisins	 du	 descripteur	 en	
question	avec	une	bonne	probabilité	de	façon	très	économe	en	temps	de	calcul.	
Le	calcul	de	similarités	entre	des	paires	d’images	peut	simplement	être	obtenu	par	une	méthode	
de	recherche	 “Brute-Force”	dans	une	base	d'images,	où	les	caractéristiques	de	l'image	requête	
sont	comparées	aux	caractéristiques	de	chacune	d’une	base	d’images	de	référence.	Un	score	de	
correspondance	entre	deux	images	peut	être	calculé	à	partir	des	N	meilleurs	matches,	et	sera	le	
critère	de	similarité	entre	les	deux	images.

## Deuxième	version	de	l’application
La	première	version	de	l’application	présente	deux	limites importantes	qui	sont	les	suivantes :	
• Impossibilité	de	mettre		à	jour	la	base	d’images pour	prendre	en	compte	des	variantes	de	
logos	ou	de		nouveaux	logos ;
• Impossibilité	 d’utiliser	 une	 base	 avec	 un	 très	 grand	 nombre	 de	 logos	 en	 raison	 de	
l’encombrement	mémoire	que	cela	impliquerait sur	le	smartphone.		
Pour	remédier	à	ces	inconvénients,	une	nouvelle	version	de	l’application	est	à	développer.	Dans	
cette	version, deux	nouveaux	principes	seront à	mettre	en	oeuvre:
- La	 reconnaissance	 d’images sera	 basée	 sur	 une	 méthode	 d’apprentissage	 à	 partir	
d’exemples.	 Cette	 méthode	 utilisera	 un	 dictionnaire	 de	 mots	 visuels	 (bag	 of	 words)	 et	
des	 classifieurs	 entrainés	 à	 partir	 d’images de	 logo	 de	 référence pour	 les	 différentes	
marques.	
- la	base	de	logos	de	référence	pour	les	marques	ne	sera pas	stockée	dans	le	mobile	mais	
sera hébergée	dans	un	serveur	Web. Il	 faudra	donc	télécharger	les	éléments	de	la	base	
(dictionnaire,	classifieurs,	index	des	images,	images)	puis	exploiter	ces	éléments	afin	de
faire	la	classification	avec	l’image	choisie	par	l’utilisateur.	
La	figure	suivante	présente	le	schéma	de	principe	de	cette	nouvelle	version.	
Figure 4 : Le schéma de principe de l’application V2 et V3
Une base	est	constituée	de	répertoires	et	de	fichiers structurés de la façon suivante :
a)	 un	 fichier	‘index.json'	contenant	les	informations	sur	les	différentes	marques	 (nom,	url	site	
web)	et	des	liens	vers	les	fichiers	associés	(classifieurs	+	images)	.
b)	 un	fichier	‘vocabulary.yml’	contenant	le	vocabulaire	visuel	appris	à	partir	des	exemples.
c)	un	répertoire	'classifiers'	contenant	les	classifiers	appris	pour	chaque	marque.	Ces	classifieurs	
sont	référencés	dans	le	fichier	‘index.json'.
d)	 un	répertoire	'train-images'	contenant	les	images	qui	ont	servi	à	l'apprentissage.	Ces	images	
sont	référencées	dans	le	fichier	‘index.json’	et	peuvent	être	utilisés	pour	présenter	des	exemples	
à	l’utilisateur.	
e)	un	répertoire	‘test-images’	contenant	des	images	pour	tester	la	classification.	
Vous	trouverez	à	l’adresse	suivante un	exemple	de	base	construite	à	l’aide	du	classifieur SIFT et	
structurée selon	ces	principes.	
http://www-rech.telecom-lille.fr/nonfreesift
Dans	 l’application	 Android,	 l’accès	 à	 une	 base	 se	 fera	 en	 utilisant	 le	 protocole	 HTTP.	 Le	
chargement	des	fichier	‘index.json’,	‘vocabulary.yml’	 et	les	différents	classifieurs	est	à	réaliser	en	
priorité.	Il	est	necessaire de faire	l’analyse	syntaxique	du fichier	‘index.json’.		Son	contenu	est	un	
objet	Json	(Javascript	Object	Notation)	structuré	par	marques,	chaque	marque	étant	réprésenté	
par	un	sous-objet javascript	muni	des	propriétés	‘brandname’,	‘url’	‘classifier’,	‘images’.	Ces	deux	
dernières	propriétés	font	références	au	fichier	classifieur	et	à	des	images.	

Votre	application	doit être	mesure	de	prendre	en	compte	l’évolution	de	la	base sur	le	serveur	:
ajout	d’une	nouvelle	marque,	mise	à	jour	du	vocabulaire ou	d’un	classifieur,	…
Au	niveau	interface	utilisateur, les	changements par	rapport	à	la	version	précédente concernent
principalement	la	présentation	du	résultat	de	la	classification.	Au	lieu	d’afficher	les	images	ayant	
une	 forte	similarité	avec	l’image	 fournie	par	l’utilisateur,	il	est	demandé	d’afficher	le	nom	de	la	
marque	la	plus	pertinente	avec	une ou	plusieurs images et	un	bouton	pour	ouvrir	la	page	du	site	
web	correspondant.	La	présentation	d’une	ou	plusieurs	images	est	utilisée	pour	que	l’utilisateur	
puisse vérifier	si	le	résultat	proposé	est	correcte ou	non.	Ces	images	de	la	marque	devront	être	
téléchargés	 en	 utilisant	 les	 éléments du	 fichier	 de	 classification (URL	 des	 images	 associés	 à	
chaque	classe).
La	technique	pour	la	reconnaissance	de	logos	dans	cette	nouvelle	version	est	plus	sophistiquée
que	dans	la	 version	précédente.	Il	s’agit	de	la	 technique	dite Sac	de	mots	visuels "Bag	of	visual	
words"	 [5].	 Dans	 cette	 technique,	 les	 descripteurs	 (genre	 SIFT)	 sont	 représentés	 par	 un	
vocabulaire	visuel,	où	un	élément	de	son	dictionnaire	est	appelé	un	mot	visuel.	Cela	est	obtenu	
par	 un	 quantificateur	 vectoriel	 (Kmean	 clustering),	 où	 la	 quantification	 consiste	 à	 assigner	
chaque	descripteur	SIFT	à	son	plus	proche	voisin	euclidien,	ce	qui	revient	à	l’assimiler	au	mot	
visuel	le	plus	proche.	La	représentation	vectorielle	de	l’image	est	calculée	comme	l’histogramme	
des	 fréquences	d’apparition	des	mots	visuels.	Cela	permet	une	représentation	compacte	et	une	
recherche	plus	efficace.

## Troisième	version	de	l’application
Pour	cette	troisième	version,	la	structure	est	la	même qu’en section	2	mais	 avec	une	différence	
liée	 à	 la	 fonctionnalité	 de	 l’application.	 Ici,	 un	 système	 de	 moteur	 de	 recherche	 remplace	 le	
système	 de	 classification.	 Par	 conséquence,	 Les	 résultats	 d'une	 requête	 sont	 classés	 par	
pertinence	ou	relevance	ranking (classement	par	pertinence	décroissante).

## Méthodologie de	travail
La	réalisation	de	l’application	est	à	faire	en	binôme.	
Avant	de	 coder,	il	est	 conseillé	 de	 réfléchir	à	l’architecture	logicielle	 de	 votre	application.	 	Il	 y	
aura	des	étapes	de	validation	pour	chaque partie.	
Pour	le	développement,	les	points	suivants	sont	à	respecter :
• Création	 d’un	 projet	 distinct	 pour	 chaque	 version	 de	 l’application (Il	 nous faut	
donc	3	versions);
• Utilisation	 du	 gestionnaire	 de	 projets	 Git	 (les	 détails	 seront	 fournis)	 pour	 le	
versionnement et	le	travail	collaboratif.
• Le	code	doit	être	suffisamment	commenté.
Vous	 avez	 la	 possibilité	 d’étendre	 le	 fonctionnement	 de	 l’application	 avec	 des	 fonctionnalités	
supplémentaires	(à	discuter	avec	l’enseignant).	
## Restitution	du	travail
Il	est	demandé	de	rendre	une	archive	.zip	contenant	les	éléments	suivants :	
• Code	source	complet	de	versions	des	projets et	l’URL	du	dépôt	Git
• Rapport	de	4	pages	max	contenant :	
o une	partie	utilisateur	
o une	partie	décrivant	l’architecture	logicielle
o une	partie	relative	au	traitement	d’image
o une	partie	bilan-conclusion
• Préparation	d’une	démonstration	pour	le	dernier	TP
