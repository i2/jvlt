Imports ICSharpCode.SharpZipLib.Zip
Imports System.IO
Imports System.Xml


Public Class Startpage

    Private _filename As String
    Private _dict As Dictionary
    Private _stats As Stats
    Private _standard As Standard


    Private Sub MenuItem1_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles menLaden.Click

        OpenFileDialog1.ShowDialog()
        _filename = OpenFileDialog1.FileName

        Me.BringToFront()

        If _filename IsNot Nothing Then
            Dim strmZipInputStream As ZipInputStream = New ZipInputStream(File.OpenRead(_filename))
            Dim objEntry As ZipEntry

            objEntry = strmZipInputStream.GetNextEntry()

            While IsNothing(objEntry) = False

                If objEntry.IsFile And objEntry.Name.StartsWith("dict") Then

                    Dim xmld As XmlDocument
                    Dim dic As XmlNode
                    Dim entrylist As XmlNodeList
                    Dim entry As XmlNode
                    Dim senselist As XmlNodeList
                    Dim sense As XmlNode

                    xmld = New XmlDocument()

                    xmld.Load(strmZipInputStream)

                    dic = xmld.DocumentElement

                    Dim dicLanguage = dic.Attributes.GetNamedItem("language").Value
                    Dim dicVersion = dic.Attributes.GetNamedItem("version").Value
                    _dict = New Dictionary(dicLanguage, dicVersion)


                    entrylist = xmld.SelectNodes("/dictionary/entry")

                    For Each entry In entrylist

                        Dim entryId = entry.Attributes.GetNamedItem("id").Value
                        Dim entryChildren = entry.ChildNodes

                        Dim entryLesson = entry.LastChild.InnerText
                        Dim entryOrth = entry.FirstChild.InnerText

                        Dim ent As Entry = New wmVLT.Entry(entryId, entryOrth, entryLesson)

                        senselist = entry.SelectNodes("sense")

                        For Each sense In senselist
                            Dim senseId = sense.Attributes.GetNamedItem("id").Value
                            Dim senseTrans = sense.FirstChild.InnerText
                            Dim senseDef = ""
                            If sense.ChildNodes.Count > 1 Then
                                senseDef = sense.LastChild.InnerText
                            End If

                            Dim sen As wmVLT.Sense = New wmVLT.Sense(senseId, senseTrans, senseDef)

                            ent.addSense(sen)
                        Next
                        _dict.addEntry(ent)
                    Next

                End If


                If objEntry.IsFile And objEntry.Name.StartsWith("stats") Then

                    Dim xmld As XmlDocument
                    Dim stat As XmlNode
                    Dim entryInfoList As XmlNodeList
                    Dim entryInfo As XmlNode

                    xmld = New XmlDocument()

                    xmld.Load(strmZipInputStream)

                    stat = xmld.DocumentElement

                    Dim statVersion = stat.Attributes.GetNamedItem("version").Value
                    _stats = New Stats(statVersion)


                    entryInfoList = xmld.SelectNodes("/stats/entry-info")

                    For Each entryInfo In entryInfoList

                        Dim entryId = entryInfo.Attributes.GetNamedItem("entry-id").Value
                        Dim entryBatch = entryInfo.Attributes.GetNamedItem("batch").Value
                        Dim entryDateAdded = entryInfo.Attributes.GetNamedItem("date-added").Value

                        Dim entryLastQueried As Date = Nothing
                        Dim entryMistakes As Integer = 0
                        Dim entryQueried As Integer = 0

                        If entryInfo.Attributes.GetNamedItem("last-queried") IsNot Nothing Then
                            entryLastQueried = entryInfo.Attributes.GetNamedItem("last-queried").Value
                        End If
                        If entryInfo.Attributes.GetNamedItem("mistakes") IsNot Nothing Then
                            entryMistakes = entryInfo.Attributes.GetNamedItem("mistakes").Value
                        End If

                        If entryInfo.Attributes.GetNamedItem("queried") IsNot Nothing Then
                            entryQueried = entryInfo.Attributes.GetNamedItem("queried").Value
                        End If


                        Dim ent As EntryInfo = New wmVLT.EntryInfo(entryId, entryBatch, entryDateAdded, entryLastQueried, entryMistakes, entryQueried)
                        _stats.enrtyInfos.Add(ent.entryId, ent)
                    Next

                End If
                'End If

                objEntry = strmZipInputStream.GetNextEntry()
            End While

            strmZipInputStream.Close()
            'Hizufuegen von weiteren Abfragestrategien
            menAbfrage.Enabled = True
            _standard = New Standard
            _standard.Init(Me, _dict, _stats)
        End If

    End Sub

    Private Sub MenuItem1_Click_1(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles menStandard.Click
        _standard.Init(Me, _dict, _stats)
        _standard.Show()
        _standard.BringToFront()
    End Sub
End Class