USE [PSCI_CCTS]
GO
/****** Object:  StoredProcedure [svm].[SP_national_training_data_concepts]    Script Date: 9/24/2018 9:24:26 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		George Michel
-- Create date: 
-- Description:	get runtype,doc,token by organ, station and no stopwords
-- =============================================
CREATE PROCEDURE [svm].[SP_national_training_data_concepts] 
@positivecount int,
@negativecount int,
	@organ varchar(256),
	@station varchar(256)
AS
BEGIN
	-- SET NOCOUNT ON for stored procedure for efficiency
		
	SET NOCOUNT ON;
	select t1.document_id, t1.concept, t2.runtype from
(
SELECT ab.document_id ,ab.anno_base_id , annoT.canonicalForm concept 
  FROM etex.anno_token annoT, etex.anno_base ab
  where ab.anno_base_id = annoT.anno_base_id
  and annoT.canonicalForm is not null
 union
 SELECT ab.document_id ,ab.anno_base_id ,  aoc.code 
  FROM etex.anno_ontology_concept aoc, etex.anno_base ab
  where ab.anno_base_id = aoc.anno_base_id
 ) t1 
 , (select top (@positivecount) ed.runtype ,ed.document_id,ed.instance_key,ed.instance_id RowID,ed.organ 
 from etex.document ed where runtype='POSITIVE' and ed.organ=@organ and  ed.instance_key=@station 
 union
 select top (@negativecount) ed.runtype ,ed.document_id,ed.instance_key,ed.instance_id RowID,ed.organ 
 from etex.document ed where runtype='NEGATIVE' and ed.organ=@organ and  ed.instance_key=@station  

 ) t2
  where t2.document_id = t1.document_id  
  and  t2.organ=@organ
  and t1.concept not in (SELECT stopword FROM etex.ref_stopword)

  order by document_id asc
END
GO
