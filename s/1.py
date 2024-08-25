from spleeter.separator import Separator

# Initialize Spleeter with 2-stem model (vocals + accompaniment)
separator = Separator('spleeter:2stems')

# Separate audio into vocals and accompaniment
separator.separate_to_file('1.mp3', 'output.lrc')
