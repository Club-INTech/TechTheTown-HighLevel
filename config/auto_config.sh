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

function parameterTest {	# Test si le paramètre $1 est égal à la valeur $2 et, le cas échéant, le remplace par $3
	if [ $(monoGrep "$1") = "$2" ]; then

		echo "WARNING: $1 is $2"
		echo "Setting $1 to $3"
		echo ""

		parameterState=$(grep $configFile -e ^"$1")
		sed -i "s/$parameterState/$(echo -n "$(echo "$parameterState" | cut -d "=" -f1 )"= $3)/" $configFile

	fi
}

configFile='./config.txt'


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

while [ "$colourConfirm" != "$newColour" ]; do 									# Boucle de confirmation

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
parameterTest "basic_detection" "false" "true"


# On refait la même pour la version du match
# Reusability, anyone?


currentMatchVersion=$(monoGrep "MATCHSCRIPT_TO_EXECUTE")
echo "Ready to launch matchScript version $currentMatchVersion"

while [ "$changeMatchScript" != 'y' -a "$changeMatchScript" != 'n' ]; do
	echo -n "Would you like to change it ? (y/n)"
	read changeMatchScript
done

if [ $changeMatchScript != 'n' ]; then
	newMatchVersion=""
	confirmMatchVersion="coucou"
	while [ "$confirmMatchVersion" != "$newMatchVersion" ]; do
		confirmMatchVersion=" "
		newMatchVersion=" "

		while [ "$newMatchVersion" != "0" -a "$newMatchVersion" != "1" -a "$newMatchVersion" != "99" ]; do
			echo -n "Choose a valid version (0/1/99): "
			read newMatchVersion
		done

		while [ "$confirmMatchVersion" != "0" -a "$confirmMatchVersion" != "1" -a "$confirmMatchVersion" != "99" ]; do
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