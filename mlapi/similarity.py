import requests
from scipy.io import wavfile
import os
import operator

def get_similar_songs(wavURL, compareDirectoryPopular, compareDirectoryUsers):
    
    #directory store store popular songs
    directory = "popular_songs_dir"
    if not os.path.exists(directory):
        os.makedirs(directory)

    #download inpuy wav file
    with open("input_file.wav", "wb") as file1:
        response = requests.get(wavURL)
        file1.write(response.content)


    #maps top similar POPULAR songs to their similarity scores
    popular_similar_songs = {}

    #download all of the popular songs
    for i in range (1,41):
        print(i)
        popular_song_id = "/audio" + str(i)
        popular_song_path = directory + "/popular_file" + str(i) + ".wav"
        with open (popular_song_path, "wb") as file2:
            #response = requests.get(compareDirectoryPopular + popular_song_id)
            response = requests.get(wavURL)
            file2.write(response.content)
        #run 
        os.system("javac fingerprint.java")
        os.system("java fingerprint input_file.wav " + popular_song_path)
        file_score = open("similarity_score.txt", "r")
        similarity_score = float(file_score.read())
        popular_similar_songs[i] = similarity_score

    sorted_popular_similar_songs = sorted(popular_similar_songs.items(), key=operator.itemgetter(1))

    print(sorted_popular_similar_songs)

    top5IDs = []
    for i in range (0, 5):
        top5IDs.append(sorted_popular_similar_songs[i][0])
    '''
    #maps top similar songs that have been uploaded to similarity scores
    top_collab_songs = {}
    '''
    #remove the input file from the input directory
    os.remove("input_file.wav")
    return top5IDs
    
def get_similar_songs_testing():
    
    os.system("javac fingerprint.java")
    os.system("java fingerprint prelude_segev.wav prelude_yoyoma.wav")
    #os.system("java fingerprint bank_account_original.wav bank_account_remix.wav")
    #os.system("java fingerprint input_file_1.wav input_file_2.wav")
    file_score = open("similarity_score.txt", "r")
    similarity_score = float(file_score.read())
    print(similarity_score)


get_similar_songs_testing()

#get_similar_songs("http://www.kozco.com/tech/c304-2.wav", " ", " ")