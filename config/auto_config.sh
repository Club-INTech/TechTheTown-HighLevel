#! /bin/bash


function colourConvert {
	if [ $1 = 'o' ]; then
		echo "orange"
	fi
	if [ $1 = 'g' ]; then
		echo "vert"
	fi
}

function monoGrep {			# Permet de récupérer l'argument dans la config (true/false/etc) du paramètre passé à la fonction ($1)
	if [ $# -eq 1 ]; then
		echo $(grep $configFile -e "^$1" | head -n 1 | cut -d "=" -f 2 | cut -d " " -f 2)
	fi
}

function parameterTest {	# Test si le paramètre $1 est égal à la valeur $2 et, le cas échéant, le remplace par $3. Demande confirmation
	answer=""

	if [ "$(monoGrep "$1")" = "$2" ]; then						# Si le paramètre est à la valeur $2, demande si on veut la changer ou pas

		echo "WARNING: $1 is $2"
		case $mode in
			"std" )
				while [ "$answer" != "y" -a "$answer" != "n" ]; do
					echo -n "Change it to standard value ($3) ? (y/n): "
					read answer
				done
				;;
			"force-yes" )
				answer="y"
				;;
			"force-no" )
				answer="n"
				;;
		esac

		if [ $answer = "y" ]; then
			echo "Setting $1 to $3"
			echo ""
			
			parameterState=$(grep $configFile -e ^"$1")
			sed -i "s/$parameterState/$(echo -n "$(echo "$parameterState" | cut -d "=" -f1 )"= $3)/" $configFile
		else
			echo "Keeping $1 at non-standard value ($2)"
			echo ""
		fi

	elif [ "$(monoGrep "$1")" = "$3" ]; then					# Si le paramètre est à la valeur $3 demande si on veut le garder ou pas

		echo "$1 is already $3"

		case $mode in
			"std" )
				while [ "$answer" != "y" -a "$answer" != "n" ]; do
					echo -n "Keep it at standard value ? (y/n): "
					read answer
				done
				;;
			"force-yes" )
				answer="y"
				;;
			"force-no" )
				answer="n"
				;;
		esac

		if [ $answer	= "y" ]; then
			echo "Keeping $1 at standard value ($3)"
			echo ""
		else
			echo "Changing $1 to non-standard value ($2)"
			echo ""

			parameterState=$(grep $configFile -e ^"$1")
			sed -i "s/$parameterState/$(echo -n "$(echo "$parameterState" | cut -d "=" -f1 )"= $2)/" $configFile
		fi

	else

		echo "WARNING: $1 value is invalid"
		echo "Setting $1 to $3"
		echo ""

		parameterState=$(grep $configFile -e ^"$1")
		sed -i "s/$parameterState/$(echo -n "$(echo "$parameterState" | cut -d "=" -f1 )"= $3)/" $configFile

	fi
}

function multiTest {		# Vérifie si $1 fait parti des éléments de $2

	if [ ! $# -eq 2 ]; then
		exit
	fi

	for toTest in ${!2}; do
		if [ "$1" = "$toTest" ]; then
			echo "0"
			exit
		fi
	done
	exit
}


if [ $# -eq 0 ]; then
	echo "Standard mode"
	mode="std"
else
	while getopts ":yYnN" option; done 							# Permet de gérer les options du script
		case $option in
			y | Y )
				echo "Forcing yes"
				mode="force-yes"
				break;;
			n | N )
				echo "Forcing no"
				mode="force-no"
				break;;
			* )
				echo "Invalid option used. Valid options are: (yYnN)"
				exit
		esac
	done
fi


configFile=""$( dirname "${BASH_SOURCE[0]}" )"/config.txt"		# Obtient le chemin d'origine du script et pas le lieu d'éxécution

if [ ! -e $configFile ]; then
	echo "ERROR: Invalid config file!"
	exit
fi

matchScriptVersions=("0" "1" "2" "99")

### COLOR CHANGE


colour=$(monoGrep "COULEUR")

if [ $colour = 'orange' ]; then
	printf '\e[48;5;208m]'	# Change la couleur du fond à orange
	echo "bite"
elif [ $colour = 'vert' ]; then
	printf '\e[48;5;118m]'	# ou à vert
fi

printf '\e[38;5;232m]'		# Ecrit le texte en noir pour la lisibilité
clear

echo "Current colour is $colour"

colourConfirm=$colour

while [ "$colourConfirm" != "$newColour" ]; do 					# Boucle de confirmation

	newColour=" "
	colourConfirm=" "

	while [ "$newColour" != "o" -a "$newColour" != "g" ]; do

		echo -n "Please choose the colour (o/g): "
		read newColour

	done

	if [ $newColour = 'o' ]; then
		printf '\e[48;5;208m]'
	elif [ $newColour = 'g' ]; then
		printf '\e[48;5;118m]'
	fi

	clear

	echo "The new colour is $(colourConvert $newColour)"

	while [ "$colourConfirm" != 'o' -a "$colourConfirm" != 'g' ]; do

		echo -n "Please confirm the chosen colour: "
		read colourConfirm
	done

done


if [ $(colourConvert $colourConfirm) = "$colour" ]; then
	echo "The colour remains $colour"
else
	echo "The colour has been changed from $colour to $(colourConvert $colourConfirm)"
	sed -i "s/= $colour/= $(colourConvert $colourConfirm)/" $configFile			# Change la couleur à la couleur choisie
fi
echo ""


### PARAMETER CHANGE


parameterTest "SIMULATION" "true" "false"
parameterTest "ATTENTE_JUMPER" "false" "true"
parameterTest "BASIC_DETECTION" "false" "true"


# On refait la même pour la version du match
# Reusability, anyone?


currentMatchVersion=$(monoGrep "MATCHSCRIPT_TO_EXECUTE")
echo "Ready to launch matchScript version $currentMatchVersion"

while [ "$changeMatchScript" != 'y' -a "$changeMatchScript" != 'n' ]; do
	echo -n "Would you like to change it ? (y/n): "
	read changeMatchScript
done

 if [ $changeMatchScript != 'n' ]; then
 	newMatchVersion=""
 	confirmMatchVersion="coucou"

 	while [ "$confirmMatchVersion" != "$newMatchVersion" ]; do
 		confirmMatchVersion=" "
 		newMatchVersion=" "
 
 		while [ ! $(multiTest "$newMatchVersion" matchScriptVersions[@]) ]; do

			echo -n "Choose a valid version ("					# Permet d'afficher les valeurs du tableau proprement
			for i in ${matchScriptVersions[@]};do
				echo -n "$i"
				if [ ! "$i" = "${matchScriptVersions[$(expr ${#matchScriptVersions[@]} - 1)]}" ]; then
					echo -n "/"
				fi
			done
			echo -n "): "
			read newMatchVersion

		done

		while [ ! $(multiTest "$confirmMatchVersion" matchScriptVersions[@] ) ]; do
			echo -n "Please confirm your selection: "
			read confirmMatchVersion
		done
	done

	if [ "$confirmMatchVersion" = "$currentMatchVersion" ]; then
		echo "Match version remains $currentMatchVersion"
	else
		matchScriptState=$(grep $configFile -e ^"MATCHSCRIPT_TO_EXECUTE")
		sed -i "s/$matchScriptState/$(echo -n "$(echo "$matchScriptState" | cut -d "=" -f1 )""= $newMatchVersion")/" $configFile
		echo "MatchVersion has been changed from $currentMatchVersion to $confirmMatchVersion"
	fi
fi

echo ""
echo "May the Force be with Aspie"

exit